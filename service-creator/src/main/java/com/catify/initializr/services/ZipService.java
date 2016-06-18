package com.catify.initializr.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beijunyi.parallelgit.filesystem.GitFileSystem;
import com.beijunyi.parallelgit.filesystem.utils.GitFileSystemBuilder;
import com.catify.initializr.domain.Domain;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 *
 * @author claus
 */
@Service
public class ZipService {

	private static final Logger LOG = Logger.getLogger(ZipService.class.getName());

	private DomainGeneratorService domainGen;

	@Autowired
	public ZipService(DomainGeneratorService domainGen) {
		this.domainGen = domainGen;
	}

	public byte[] zipFileSystem(Domain domain) throws IOException, GitAPIException {

		byte[] result = null;
		
		File repoPath = new File("gitRepository");
		Git.init().setDirectory(repoPath).call();

		Git git = Git.open(repoPath);
		git.commit().setAll(true).setMessage("initial commit").call();
		Repository repository = git.getRepository();

		// git file system
		try (GitFileSystem gfs = GitFileSystemBuilder.forRevision("master", repository)) {
			this.domainGen.createDomain(domain, gfs);

			// virtual file system
			try (FileSystem vfs = Jimfs.newFileSystem(Configuration.unix())) {
				Path zipPath = vfs.getPath("/initializedService.zip");

				Map<String, String> env = new HashMap<>();
				env.put("create", "true");
				URI uri = URI.create("jar:" + zipPath.toUri());

				// zip file system
				try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
					this.copyToZip(gfs, zipfs);
					this.copyToZip(Paths.get("gitRepository/.git"), Paths.get("gitRepository"), zipfs);
				}

				result = Files.readAllBytes(zipPath);
			}
		} finally {
			FileUtils.deleteQuietly(repoPath);
		}

		return result;
	}

	private void copyToZip(FileSystem source, FileSystem dest) throws IOException {
		Iterable<Path> root = source.getRootDirectories();
		for (Path path : root) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					LOG.finest(file.toString());
					Files.copy(file, dest.getPath(file.toString()), StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					LOG.finest(dir.toString());
					Files.createDirectories(dest.getPath(dir.toString()));
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	private void copyToZip(Path source, Path base, FileSystem dest) throws IOException {
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				LOG.finest(file.toString());
				Files.copy(file, dest.getPath(base.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				LOG.finest(dir.toString());
				Files.createDirectory(dest.getPath(base.relativize(dir).toString()));
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
