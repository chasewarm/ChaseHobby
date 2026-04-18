package com.example.chasehobby;

public class BookContent extends ContentItem {
    private String author;
    private Integer pageCount;
    private String fullText;
    private int currentPage = 0;

    public BookContent() {
        setMediaType("book");
    }

    public String getAuthor() {
        return author != null ? author : getAuthorDirector();
    }

    public void setAuthor(String author) {
        this.author = author;
        setAuthorDirector(author);
    }

    @Override
    public Integer getPageCount() {
        return pageCount != null ? pageCount : super.getPageCount();
    }

    @Override
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
        super.setPageCount(pageCount);
    }

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }


    public String getPageText(int pageNumber) {
        if (fullText == null || fullText.isEmpty()) return "";
        int pageSize = 2000;
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, fullText.length());
        if (start >= fullText.length()) return "";
        return fullText.substring(start, end);
    }


    public int getTotalPages() {
        if (fullText == null || fullText.isEmpty()) return 0;
        return (int) Math.ceil(fullText.length() / 2000.0);
    }


    public String getChapterText(int chapterIndex) {
        if (fullText == null || fullText.isEmpty()) return "";

        String[] chapters = splitIntoChapters();
        if (chapterIndex >= 0 && chapterIndex < chapters.length) {
            return chapters[chapterIndex];
        }
        return fullText;
    }

    public int getChapterCount() {
        if (fullText == null || fullText.isEmpty()) return 0;
        return splitIntoChapters().length;
    }


    public String[] getChapterTitles() {
        if (fullText == null || fullText.isEmpty()) return new String[0];

        String[] chapters = splitIntoChapters();
        String[] titles = new String[chapters.length];

        for (int i = 0; i < chapters.length; i++) {
            String[] lines = chapters[i].split("\n", 2);
            titles[i] = lines[0].trim();
            if (titles[i].isEmpty() || titles[i].length() > 100) {
                titles[i] = "Глава " + (i + 1);
            }
        }

        return titles;
    }


    private String[] splitIntoChapters() {
        return fullText.split("(?=Глава \\d+)|(?=Chapter \\d+)|(?=Пролог)|(?=Эпилог)");
    }
    public boolean hasValidText() {
        return fullText != null && !fullText.trim().isEmpty();
    }
}