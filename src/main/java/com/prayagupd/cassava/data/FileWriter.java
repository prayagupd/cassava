package com.prayagupd.cassava.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FileWriter {

    private String file;
    private FileOutputStream fileOutputStream;

    public FileWriter(String fileName) throws FileNotFoundException {
        this.file = fileName;
        var file = new File(fileName);
        this.fileOutputStream = new FileOutputStream(file);
    }

    public static CompletableFuture<FileWriter> openStream(String file) {
        CompletableFuture<CompletableFuture<FileWriter>> f = CompletableFuture.supplyAsync(() -> {
            try {
                return CompletableFuture.completedFuture(new FileWriter(file));
            } catch (FileNotFoundException e) {
                return CompletableFuture.failedFuture(e);
            }
        });
        return f.thenCompose(Function.identity());
    }

    public CompletableFuture<FileWriter> append(String data) {
        CompletableFuture<CompletableFuture<FileWriter>> f = CompletableFuture.supplyAsync(() -> {
            try {
                fileOutputStream.write(data.getBytes());
                return CompletableFuture.completedFuture(this);
            } catch (IOException e) {
                return CompletableFuture.failedFuture(new RuntimeException("could not write to file", e));
            }
        });

        return f.thenCompose(Function.identity());
    }

    public CompletableFuture<FileWriter> flushStream() {
        CompletableFuture<CompletableFuture<FileWriter>> f = CompletableFuture.supplyAsync(() -> {
            try {
                fileOutputStream.flush();
                return CompletableFuture.completedFuture(this);
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
        });
        return f.thenCompose(Function.identity());
    }

    public CompletableFuture<Boolean> closeStream() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                fileOutputStream.close();
                return CompletableFuture.completedFuture(Boolean.TRUE);
            } catch (IOException e) {
                return CompletableFuture.completedFuture(Boolean.TRUE);
            }
        }).thenCompose(Function.identity());
    }
}
