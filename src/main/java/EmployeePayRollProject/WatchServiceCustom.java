package EmployeePayRollProject;

import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.io.*;

public class WatchServiceCustom {
	private final WatchService watcher;
	private final HashMap<WatchKey, Path> directoryWatcher;

	WatchServiceCustom(Path directory) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.directoryWatcher = new HashMap<WatchKey, Path>();
		registerDirs(directory);
	}

	private void registerDirs(final Path dir) throws IOException {
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
				registerDirectoryWatchers(directory);
				return FileVisitResult.CONTINUE;
			}

		});

	}

	private void registerDirectoryWatchers(Path directory) throws IOException {
		WatchKey key = directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		directoryWatcher.put(key, directory);

	}

	void processEvents() {
		while (true) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException e) {
				return;
			}

			Path directory = directoryWatcher.get(key);
			if (directory == null)
				continue;
			for (WatchEvent<?> ent : key.pollEvents()) {
				WatchEvent.Kind kind = ent.kind();
				Path nameVar = ((WatchEvent<Path>) ent).context();
				Path child = directory.resolve(nameVar);
				System.out.format("%s: %s\n", ent.kind().name(), child);

				if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child))
							registerDirs(child);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE))
					if (Files.isDirectory(child))
						directoryWatcher.remove(key);

			}

			boolean valid = key.reset();
			if (!valid) {
				directoryWatcher.remove(key);
				if (directoryWatcher.isEmpty())
					break;
			}
		}
	}

}
