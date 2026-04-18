package com.example.chasehobby;

public class MediaPreferences {
    private int booksPriority;
    private int moviesPriority;
    private int seriesPriority;
    private int podcastsPriority;
    private int gamesPriority;

    public MediaPreferences(int books, int movies, int series, int podcasts, int games) {
        this.booksPriority = books;
        this.moviesPriority = movies;
        this.seriesPriority = series;
        this.podcastsPriority = podcasts;
        this.gamesPriority = games;
    }

    // Геттеры
    public int getBooksPriority() { return booksPriority; }
    public int getMoviesPriority() { return moviesPriority; }
    public int getSeriesPriority() { return seriesPriority; }
    public int getPodcastsPriority() { return podcastsPriority; }
    public int getGamesPriority() { return gamesPriority; }

    // Сеттеры
    public void setBooksPriority(int booksPriority) { this.booksPriority = booksPriority; }
    public void setMoviesPriority(int moviesPriority) { this.moviesPriority = moviesPriority; }
    public void setSeriesPriority(int seriesPriority) { this.seriesPriority = seriesPriority; }
    public void setPodcastsPriority(int podcastsPriority) { this.podcastsPriority = podcastsPriority; }
    public void setGamesPriority(int gamesPriority) { this.gamesPriority = gamesPriority; }

    /**
     * Получает приоритет для конкретного типа медиа
     * @param mediaType - тип медиа ("book", "movie", "series", "podcast", "game")
     * @return число от 1 до 5
     */
    public int getPriorityForMediaType(String mediaType) {
        if (mediaType == null) return 1;

        switch (mediaType.toLowerCase()) {
            case "book":
                return booksPriority;
            case "movie":
                return moviesPriority;
            case "series":
                return seriesPriority;
            case "podcast":
                return podcastsPriority;
            case "game":
                return gamesPriority;
            default:
                return 1; // По умолчанию низкий приоритет
        }
    }

    @Override
    public String toString() {
        return String.format("Книги: %d/5, Фильмы: %d/5, Сериалы: %d/5, Подкасты: %d/5, Игры: %d/5",
                booksPriority, moviesPriority, seriesPriority, podcastsPriority, gamesPriority);
    }
}