package com.example.chasehobby;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Point2D;
import javafx.stage.Popup;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SearchController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreFilter;
    @FXML private ComboBox<String> mediaTypeFilter;
    @FXML private VBox resultsContainer;
    @FXML private Label resultsLabel;

    private Label emptyPlaceholder;
    private AutoCompleteService autoCompleteService;

    // Компоненты для автодополнения
    private Popup autoCompletePopup;
    private ListView<String> suggestionsList;
    private List<String> currentSuggestions;

    @FXML
    public void initialize() {
        // Инициализация сервиса автодополнения
        autoCompleteService = AutoCompleteService.getInstance();

        // Инициализация автодополнения
        initAutoComplete();

        // Создаем плейсхолдер для пустого результата
        emptyPlaceholder = new Label("Введите поисковый запрос или используйте фильтры");
        emptyPlaceholder.setTextFill(javafx.scene.paint.Color.web("#95A5A6"));
        emptyPlaceholder.setFont(javafx.scene.text.Font.font(14));
        emptyPlaceholder.setMaxWidth(Double.MAX_VALUE);
        emptyPlaceholder.setAlignment(javafx.geometry.Pos.CENTER);
        emptyPlaceholder.setPadding(new javafx.geometry.Insets(50, 0, 50, 0));

        // Инициализация фильтров
        initializeFilters();

        // Показываем плейсхолдер при старте
        showEmptyPlaceholder();

        // Добавляем обработчик клавиш
        setupKeyHandlers();
    }

    /**
     * Инициализация компонентов автодополнения
     */
    private void initAutoComplete() {
        autoCompletePopup = new Popup();
        suggestionsList = new ListView<>();
        suggestionsList.setPrefWidth(300);
        suggestionsList.setPrefHeight(200);
        suggestionsList.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                "-fx-border-radius: 5; -fx-background-radius: 5;");

        // Обработчик выбора подсказки
        suggestionsList.setOnMouseClicked(e -> {
            if (suggestionsList.getSelectionModel().getSelectedItem() != null) {
                selectSuggestion(suggestionsList.getSelectionModel().getSelectedItem());
            }
        });

        autoCompletePopup.getContent().add(suggestionsList);

        // Слушатель изменения текста
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                showSuggestions(newVal);
            } else {
                autoCompletePopup.hide();
            }
        });

        // Обработка нажатий клавиш в поле поиска
        searchField.setOnKeyPressed(this::handleSearchFieldKeys);
    }

    /**
     * Обработка клавиш в поле поиска для навигации по подсказкам
     */
    private void handleSearchFieldKeys(KeyEvent event) {
        if (!autoCompletePopup.isShowing()) return;

        if (event.getCode() == KeyCode.DOWN) {
            // Вниз по списку
            int index = suggestionsList.getSelectionModel().getSelectedIndex();
            if (index < suggestionsList.getItems().size() - 1) {
                suggestionsList.getSelectionModel().select(index + 1);
                suggestionsList.scrollTo(index + 1);
            } else if (index == -1 && !suggestionsList.getItems().isEmpty()) {
                suggestionsList.getSelectionModel().select(0);
            }
            event.consume();
        } else if (event.getCode() == KeyCode.UP) {
            // Вверх по списку
            int index = suggestionsList.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                suggestionsList.getSelectionModel().select(index - 1);
                suggestionsList.scrollTo(index - 1);
            }
            event.consume();
        } else if (event.getCode() == KeyCode.ENTER) {
            // Выбор подсказки
            String selected = suggestionsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectSuggestion(selected);
            }
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            // Закрыть подсказки
            autoCompletePopup.hide();
        }
    }

    /**
     * Показ подсказок
     */
    private void showSuggestions(String text) {
        currentSuggestions = autoCompleteService.getSuggestionsAsList(text, 8);
        if (currentSuggestions.isEmpty()) {
            autoCompletePopup.hide();
            return;
        }

        suggestionsList.getItems().clear();
        suggestionsList.getItems().addAll(currentSuggestions);
        suggestionsList.getSelectionModel().clearSelection();

        // Позиционирование подсказок под полем ввода
        Point2D point = searchField.localToScreen(0, searchField.getHeight());
        if (point != null) {
            autoCompletePopup.show(searchField, point.getX(), point.getY());
        }
    }

    /**
     * Выбор подсказки
     */
    private void selectSuggestion(String suggestion) {
        searchField.setText(suggestion);
        searchField.positionCaret(suggestion.length());
        autoCompletePopup.hide();
        handleSearch();
    }

    /**
     * Настройка обработчиков клавиш
     */
    private void setupKeyHandlers() {
        // Поиск по нажатию Enter
        searchField.setOnAction(event -> handleSearch());

        // Сброс по Escape
        searchField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (autoCompletePopup.isShowing()) {
                    autoCompletePopup.hide();
                    event.consume();
                } else {
                    handleReset();
                }
            }
        });
    }

    private void initializeFilters() {
        genreFilter.getItems().clear();
        genreFilter.getItems().addAll(getAllGenres());
        genreFilter.setValue("Все жанры");

        mediaTypeFilter.getItems().clear();
        mediaTypeFilter.getItems().addAll(
                "Все типы", "Книги", "Фильмы", "Сериалы", "Подкасты", "Игры"
        );
        mediaTypeFilter.setValue("Все типы");
    }

    private List<String> getAllGenres() {
        List<String> genres = UserService.getAllGenres();
        genres.add(0, "Все жанры");
        return genres;
    }

    private void showEmptyPlaceholder() {
        resultsContainer.getChildren().clear();
        resultsContainer.getChildren().add(emptyPlaceholder);
    }

    @FXML
    private void handleSearch() {
        // Скрываем подсказки
        autoCompletePopup.hide();

        String query = searchField.getText().trim();
        String genre = genreFilter.getValue();
        String mediaType = mediaTypeFilter.getValue();

        String genreDb = convertGenreToDb(genre);
        String mediaTypeDb = convertMediaTypeToDb(mediaType);

        List<ContentItem> results;
        if (query.isEmpty() && "Все жанры".equals(genre) && "Все типы".equals(mediaType)) {
            results = ContentService.getPopularContent();
        } else {
            results = ContentService.searchContent(query, genreDb, mediaTypeDb);
        }

        displayResults(results);
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        autoCompletePopup.hide();
        genreFilter.setValue("Все жанры");
        mediaTypeFilter.setValue("Все типы");
        resultsContainer.getChildren().clear();
        resultsLabel.setText("Результаты поиска");
        showEmptyPlaceholder();
    }

    private String convertGenreToDb(String genre) {
        if (genre == null || genre.equals("Все жанры")) {
            return "";
        }
        return genre;
    }

    private String convertMediaTypeToDb(String mediaType) {
        if (mediaType == null || mediaType.equals("Все типы")) {
            return "";
        }
        switch (mediaType) {
            case "Книги": return "book";
            case "Фильмы": return "movie";
            case "Сериалы": return "series";
            case "Подкасты": return "podcast";
            case "Игры": return "game";
            default: return "";
        }
    }

    private void displayResults(List<ContentItem> results) {
        resultsContainer.getChildren().clear();

        if (results.isEmpty()) {
            resultsLabel.setText("Ничего не найдено");
            Label noResultsLabel = new Label("Попробуйте изменить параметры поиска");
            noResultsLabel.setTextFill(javafx.scene.paint.Color.web("#95A5A6"));
            noResultsLabel.setFont(javafx.scene.text.Font.font(14));
            noResultsLabel.setMaxWidth(Double.MAX_VALUE);
            noResultsLabel.setAlignment(javafx.geometry.Pos.CENTER);
            noResultsLabel.setPadding(new javafx.geometry.Insets(20, 0, 20, 0));
            resultsContainer.getChildren().add(noResultsLabel);
            return;
        }

        resultsLabel.setText("Найдено: " + results.size() + " результатов");

        for (ContentItem item : results) {
            VBox resultBox = createResultBox(item);
            ContentItem currentItem = item;
            resultBox.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1) {
                    openContentPlayer(currentItem);
                }
            });
            resultsContainer.getChildren().add(resultBox);
        }
    }

    private VBox createResultBox(ContentItem item) {
        VBox resultBox = new VBox(5);
        resultBox.setStyle("-fx-padding: 15; -fx-background-color: white; " +
                "-fx-background-radius: 8; -fx-border-color: #E0E0E0; " +
                "-fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // Заголовок с иконкой
        Label titleLabel = new Label(item.getMediaIcon() + " " + item.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Детали
        Label detailsLabel = new Label(
                String.format("%s • %s • ⭐ %.1f",
                        item.getMediaTypeDisplayName(),
                        item.getGenre(),
                        item.getRating())
        );
        detailsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #5D6D7E;");

        // Описание
        String description = item.getDescription();
        if (description != null && description.length() > 150) {
            description = description.substring(0, 150) + "...";
        }
        Label descriptionLabel = new Label(description);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C3E50;");
        descriptionLabel.setWrapText(true);

        resultBox.getChildren().addAll(titleLabel, detailsLabel, descriptionLabel);

        // Эффект при наведении
        resultBox.setOnMouseEntered(e ->
                resultBox.setStyle("-fx-padding: 15; -fx-background-color: white; " +
                        "-fx-background-radius: 8; -fx-border-color: #3498DB; " +
                        "-fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(52,152,219,0.2), 10, 0, 0, 3);")
        );
        resultBox.setOnMouseExited(e ->
                resultBox.setStyle("-fx-padding: 15; -fx-background-color: white; " +
                        "-fx-background-radius: 8; -fx-border-color: #E0E0E0; " +
                        "-fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);")
        );

        return resultBox;
    }

    private void openContentPlayer(ContentItem item) {
        try {
            ContentItem contentToShow;
            if (item.getMediaType().equals("book")) {
                BookContent fullBook = ContentService.getBookContent(item.getId());
                contentToShow = (fullBook != null) ? fullBook : item;
            } else {
                contentToShow = item;
            }

            ContentItem finalContentToShow = contentToShow;

            if (userData != null && userData.getUserId() > 0) {
                ContentService.addToHistory(userData.getUserId(), item.getId(), 0);
            }

            SessionManager.getInstance().navigateTo("ContentPlayer-view.fxml", item.getTitle(), 1300, 800,
                    (ContentPlayerController controller) -> controller.setContent(finalContentToShow, userData));

        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка загрузки плеера: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            SessionManager.getInstance().navigateTo("Main-view.fxml", "Главная", 1200, 800,
                    (MainController controller) -> {
                        if (userData != null) {
                            controller.setUserData(userData);
                        }
                        controller.loadData();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка возврата на главную");
        }
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
        initializeFilters();
    }

    public void setSearchQuery(String query) {
        if (query != null && !query.isEmpty()) {
            searchField.setText(query);
            handleSearch();
        }
    }

    /**
     * Обновление кэша автодополнения
     */
    public void refreshAutoCompleteCache() {
        if (autoCompleteService != null) {
            autoCompleteService.refreshCache();
        }
    }
}