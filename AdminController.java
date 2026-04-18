package com.example.chasehobby;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.io.IOException;
import java.util.List;

public class AdminController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private TabPane adminTabs;

    // Вкладка контента
    @FXML private TableView<ContentItem> contentTable;
    @FXML private TableColumn<ContentItem, String> titleColumn;
    @FXML private TableColumn<ContentItem, String> typeColumn;
    @FXML private TableColumn<ContentItem, String> genreColumn;
    @FXML private TableColumn<ContentItem, Double> ratingColumn;

    // Форма добавления/редактирования контента
    @FXML private TextField contentTitleField;
    @FXML private TextArea contentDescriptionField;
    @FXML private ComboBox<String> contentTypeCombo;
    @FXML private ComboBox<String> contentGenreCombo;
    @FXML private TextField contentAuthorField;
    @FXML private TextField contentYearField;
    @FXML private TextField contentRatingField;
    @FXML private TextField contentPosterUrlField;
    @FXML private TextField contentTrailerUrlField;
    @FXML private TextField contentWatchUrlField;
    @FXML private TextField contentListenUrlField;
    @FXML private TextField contentDownloadUrlField;

    // Специфичные для типа поля
    @FXML private VBox bookFields;
    @FXML private TextField bookPageCountField;
    @FXML private TextArea bookTextField;
    @FXML private TextField bookAuthorField;

    @FXML private VBox movieFields;
    @FXML private TextField movieDurationField;

    @FXML private VBox seriesFields;
    @FXML private TextField seriesSeasonsField;
    @FXML private TextField seriesEpisodesField;

    @FXML private VBox gameFields;
    @FXML private TextField gamePlatformsField;

    // Вкладка пользователей
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> loginColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;

    // Вкладка жанров
    @FXML private ListView<String> genresList;
    @FXML private TextField newGenreField;

    // Вкладка статистики
    @FXML private Label totalUsersLabel;
    @FXML private Label totalContentLabel;
    @FXML private Label totalBooksLabel;
    @FXML private Label totalMoviesLabel;
    @FXML private Label totalSeriesLabel;
    @FXML private Label totalPodcastsLabel;
    @FXML private Label totalGamesLabel;
    @FXML private ListView<String> popularContentList;

    private ContentItem selectedContent;
    private User selectedUser;

    @FXML
    public void initialize() {
        setupContentTable();
        setupUserTable();
        setupContentTypeCombo();
        loadGenres();
        loadStatistics();
    }

    private void setupContentTable() {
        titleColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        typeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMediaTypeDisplayName()));
        genreColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGenre()));
        ratingColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getRating()).asObject());

        contentTable.getItems().addAll(ContentService.getPopularContent());

        contentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedContent = newVal;
                        loadContentForEditing(newVal);
                    }
                });
    }

    private void setupUserTable() {
        usernameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        loginColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLogin()));
        roleColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        createdAtColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt()));

        loadUsers();
    }

    private void setupContentTypeCombo() {
        contentTypeCombo.getItems().addAll("Книга", "Фильм", "Сериал", "Подкаст", "Игра");
        contentTypeCombo.setValue("Книга");
        contentTypeCombo.setOnAction(e -> updateTypeSpecificFields());
        updateTypeSpecificFields();
    }

    private void updateTypeSpecificFields() {
        String type = contentTypeCombo.getValue();
        bookFields.setVisible("Книга".equals(type));
        bookFields.setManaged("Книга".equals(type));
        movieFields.setVisible("Фильм".equals(type) || "Сериал".equals(type));
        movieFields.setManaged("Фильм".equals(type) || "Сериал".equals(type));
        seriesFields.setVisible("Сериал".equals(type));
        seriesFields.setManaged("Сериал".equals(type));
        gameFields.setVisible("Игра".equals(type));
        gameFields.setManaged("Игра".equals(type));
    }

    private void loadContentForEditing(ContentItem content) {
        contentTitleField.setText(content.getTitle());
        contentDescriptionField.setText(content.getDescription());
        contentTypeCombo.setValue(content.getMediaTypeDisplayName());
        contentGenreCombo.setValue(content.getGenre());
        contentAuthorField.setText(content.getAuthorDirector());
        contentYearField.setText(content.getReleaseYear() != null ? content.getReleaseYear().toString() : "");
        contentRatingField.setText(String.valueOf(content.getRating()));
        contentPosterUrlField.setText(content.getPosterUrl());
        contentTrailerUrlField.setText(content.getTrailerUrl());
        contentWatchUrlField.setText(content.getWatchUrl());
        contentListenUrlField.setText(content.getListenUrl());
        contentDownloadUrlField.setText(content.getDownloadUrl());

        // Загрузка специфичных полей
        if (content instanceof BookContent) {
            BookContent book = (BookContent) content;
            bookPageCountField.setText(book.getPageCount() != null ? book.getPageCount().toString() : "");
            bookTextField.setText(book.getFullText());
        } else if (content instanceof MovieContent) {
            MovieContent movie = (MovieContent) content;
            movieDurationField.setText(movie.getDurationMinutes() != null ? movie.getDurationMinutes().toString() : "");
        } else if (content instanceof SeriesContent) {
            SeriesContent series = (SeriesContent) content;
            movieDurationField.setText(series.getDurationMinutes() != null ? series.getDurationMinutes().toString() : "");
            seriesSeasonsField.setText(series.getSeasons() != null ? series.getSeasons().toString() : "");
            seriesEpisodesField.setText(series.getEpisodes() != null ? series.getEpisodes().toString() : "");
        } else if (content instanceof GameContent) {
            GameContent game = (GameContent) content;
            gamePlatformsField.setText(game.getPlatforms());
        }
    }

    @FXML
    private void handleAddContent() {
        clearContentForm();
        selectedContent = null;
    }

    @FXML
    private void handleSaveContent() {
        // Валидация
        if (contentTitleField.getText().trim().isEmpty()) {
            showError("Введите название");
            return;
        }

        // Создание или обновление контента
        ContentItem content = selectedContent != null ? selectedContent : createContentByType();

        content.setTitle(contentTitleField.getText().trim());
        content.setDescription(contentDescriptionField.getText().trim());
        content.setGenre(contentGenreCombo.getValue());
        content.setAuthorDirector(contentAuthorField.getText().trim());

        try {
            if (!contentYearField.getText().trim().isEmpty()) {
                content.setReleaseYear(Integer.parseInt(contentYearField.getText().trim()));
            }
            if (!contentRatingField.getText().trim().isEmpty()) {
                content.setRating(Double.parseDouble(contentRatingField.getText().trim()));
            }
        } catch (NumberFormatException e) {
            showError("Неверный формат числа");
            return;
        }

        content.setPosterUrl(contentPosterUrlField.getText().trim());
        content.setTrailerUrl(contentTrailerUrlField.getText().trim());
        content.setWatchUrl(contentWatchUrlField.getText().trim());
        content.setListenUrl(contentListenUrlField.getText().trim());
        content.setDownloadUrl(contentDownloadUrlField.getText().trim());

        // Если это книга, заполняем специфичные поля
        if (content instanceof BookContent) {
            BookContent book = (BookContent) content;

            // Устанавливаем автора книги (если не указан, используем author_director)
            if (!bookAuthorField.getText().trim().isEmpty()) {
                book.setAuthor(bookAuthorField.getText().trim());
            }

            // Устанавливаем количество страниц
            if (!bookPageCountField.getText().trim().isEmpty()) {
                try {
                    book.setPageCount(Integer.parseInt(bookPageCountField.getText().trim()));
                } catch (NumberFormatException e) {
                    showError("Неверный формат количества страниц");
                    return;
                }
            }

            // Устанавливаем текст книги
            String bookText = bookTextField.getText().trim();
            if (bookText.isEmpty()) {
                showError("Для книги необходимо ввести текст");
                return;
            }
            book.setFullText(bookText);
        }

        // Сохранение в БД
        boolean success = ContentService.saveContent(content);

        if (success) {
            showInfo("Контент сохранен");
            refreshContentTable();
            clearContentForm();
        } else {
            showError("Ошибка сохранения");
        }
    }

    private ContentItem createContentByType() {
        String type = contentTypeCombo.getValue();
        switch (type) {
            case "Книга": return new BookContent();
            case "Фильм": return new MovieContent();
            case "Сериал": return new SeriesContent();
            case "Подкаст": return new PodcastContent();
            case "Игра": return new GameContent();
            default: return new ContentItem();
        }
    }

    @FXML
    private void handleDeleteContent() {
        if (selectedContent == null) {
            showError("Выберите контент для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удалить контент?");
        confirm.setContentText("Вы уверены, что хотите удалить \"" + selectedContent.getTitle() + "\"?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = ContentService.deleteContent(selectedContent.getId());
            if (success) {
                showInfo("Контент удален");
                refreshContentTable();
                clearContentForm();
            } else {
                showError("Ошибка удаления");
            }
        }
    }

    @FXML
    private void handleBlockUser() {
        if (selectedUser == null) {
            showError("Выберите пользователя");
            return;
        }
        // Логика блокировки пользователя
        showInfo("Пользователь заблокирован");
    }

    @FXML
    private void handleResetPassword() {
        if (selectedUser == null) {
            showError("Выберите пользователя");
            return;
        }
        // Логика сброса пароля
        showInfo("Пароль сброшен");
    }

    @FXML
    private void handleAddGenre() {
        String genreName = newGenreField.getText().trim();
        if (genreName.isEmpty()) {
            showError("Введите название жанра");
            return;
        }

        boolean success = GenreService.addGenre(genreName);
        if (success) {
            showInfo("Жанр добавлен");
            loadGenres();
            newGenreField.clear();
        } else {
            showError("Ошибка добавления жанра");
        }
    }

    @FXML
    private void handleDeleteGenre() {
        String selectedGenre = genresList.getSelectionModel().getSelectedItem();
        if (selectedGenre == null) {
            showError("Выберите жанр");
            return;
        }

        boolean success = GenreService.deleteGenre(selectedGenre);
        if (success) {
            showInfo("Жанр удален");
            loadGenres();
        } else {
            showError("Невозможно удалить жанр (используется в контенте)");
        }
    }

    private void loadGenres() {
        genresList.getItems().clear();
        genresList.getItems().addAll(UserService.getAllGenres());
    }

    private void loadUsers() {
        userTable.getItems().clear();
        userTable.getItems().addAll(UserService.getAllUsers());
    }

    private void loadStatistics() {
        // Загрузка статистики
        totalUsersLabel.setText(String.valueOf(UserService.getUserCount()));
        totalContentLabel.setText(String.valueOf(ContentService.getContentCount()));
        totalBooksLabel.setText(String.valueOf(ContentService.getContentCountByType("book")));
        totalMoviesLabel.setText(String.valueOf(ContentService.getContentCountByType("movie")));
        totalSeriesLabel.setText(String.valueOf(ContentService.getContentCountByType("series")));
        totalPodcastsLabel.setText(String.valueOf(ContentService.getContentCountByType("podcast")));
        totalGamesLabel.setText(String.valueOf(ContentService.getContentCountByType("game")));

        // Популярный контент
        popularContentList.getItems().clear();
        ContentService.getPopularContentWithStats().forEach(item ->
                popularContentList.getItems().add(item.getTitle() + " (⭐" + item.getRating() + ")")
        );
    }

    private void refreshContentTable() {
        contentTable.getItems().clear();
        contentTable.getItems().addAll(ContentService.getAllContent());
    }

    private void clearContentForm() {
        contentTitleField.clear();
        contentDescriptionField.clear();
        contentAuthorField.clear();
        contentYearField.clear();
        contentRatingField.clear();
        contentPosterUrlField.clear();
        contentTrailerUrlField.clear();
        contentWatchUrlField.clear();
        contentListenUrlField.clear();
        contentDownloadUrlField.clear();
        bookPageCountField.clear();
        bookTextField.clear();
        movieDurationField.clear();
        seriesSeasonsField.clear();
        seriesEpisodesField.clear();
        gamePlatformsField.clear();
    }

    @FXML
    private void goBack() {
        try {
            System.out.println("Возврат на главную из админ-панели");
            SessionManager.getInstance().navigateTo("Main-view.fxml", "Главная", 1200, 800,
                    (MainController controller) -> {
                        if (userData != null) {
                            controller.setUserData(userData);
                        }
                        controller.loadData();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка возврата");
        }
    }
}