package filesorter.invariants.byFilename.FoldersInSyncInvariant;

class SyncFile {
    final String fileNoPath;
    private final long hash;

    public SyncFile(String fileNoPath, long hash) {

        this.fileNoPath = fileNoPath;
        this.hash = hash;
    }

    public boolean contentEquals(SyncFile file2) {
        return fileNoPath.contentEquals(file2.fileNoPath);
    }

    public boolean hashEquals(SyncFile file2) {
        return hash == file2.hash;
    }

    @Override
    public String toString() {
        return "SyncFile{" +
                "fileNoPath='" + fileNoPath + '\'' +
                ", hash=" + hash +
                '}';
    }
}
