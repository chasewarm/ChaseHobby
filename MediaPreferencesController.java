package com.example.chasehobby;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import java.io.IOException;
import java.util.List;

public class MediaPreferencesController {

    @FXML private VBox mediaContainer;
    @FXML private Button saveButton;
    @FXML private Label errorLabel;

    private String userLogin;
    private List<String> selectedGenres;

    private Slider booksSlider;
    private Slider moviesSlider;
    private Slider seriesSlider;
    private Slider podcastsSlider;
    private Slider gamesSlider;

    public void setUserData(String login, List<String> genres) {
        this.userLogin = login;
        this.selectedGenres = genres;
        createSliders();
    }

    private void createSliders() {
        mediaContainer.getChildren().clear();

        booksSlider = createSingleSlider("Книги", 3);
        moviesSlider = createSingleSlider("Фильмы", 3);
        seriesSlider = createSingleSlider("Сериалы", 3);
        podcastsSlider = createSingleSlider("Подкасты", 3);
        gamesSlider = createSingleSlider("Игры", 3);
    }

    private Slider createSingleSlider(String labelText, int defaultValue) {
        VBox box = new VBox(5);
        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);"
        );
        box.setMaxWidth(500);

        Label label = UIUtils.createTitleLabel(labelText + ":", 16);

        Slider slider = new Slider(1, 5, defaultValue);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.setPrefWidth(300);

        Label valueLabel = new Label(String.valueOf(defaultValue));
        valueLabel.setStyle(
                "-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-min-width: 30;"
        );

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            valueLabel.setText(String.valueOf(newVal.intValue()));
        });

        HBox sliderBox = new HBox(10, slider, valueLabel);
        sliderBox.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(label, sliderBox);
        mediaContainer.getChildren().add(box);

        return slider;
    }

    @FXML
    private void handleSave() {
        if (!validateSliders()) return;

        int booksValue = (int) booksSlider.getValue();
        int moviesValue = (int) moviesSlider.getValue();
        int seriesValue = (int) seriesSlider.getValue();
        int podcastsValue = (int) podcastsSlider.getValue();
        int gamesValue = (int) gamesSlider.getValue();

        MediaPreferences prefs = new MediaPreferences(
                booksValue, moviesValue, seriesValue, podcastsValue, gamesValue
        );

        // Получаем пользователя по логину
        User user = UserService.getUserByLogin(userLogin);
        if (user != null) {
            // Сохраняем предпочтения
            UserService.saveMediaPreferences(user.getId(), prefs);
            System.out.println("Сохранены предпочтения для пользователя: " + userLogin);

            // Получаем полные данные пользователя
            UserData userData = UserService.getUserFullData(user.getId());
            if (userData != null) {
                SessionManager.getInstance().setCurrentUser(userData);
                openMainScreen();
            } else {
                showError("Ошибка загрузки данных пользователя");
            }
        } else {
            showError("Пользователь не найден");
        }
    }

    private boolean validateSliders() {
        if (booksSlider == null || moviesSlider == null || seriesSlider == null ||
                podcastsSlider == null || gamesSlider == null) {
            showError("Ошибка инициализации слайдеров");
            return false;
        }

        int booksValue = (int) booksSlider.getValue();
        int moviesValue = (int) moviesSlider.getValue();
        int seriesValue = (int) seriesSlider.getValue();
        int podcastsValue = (int) podcastsSlider.getValue();
        int gamesValue = (int) gamesSlider.getValue();

        if (booksValue == 1 && moviesValue == 1 && seriesValue == 1 &&
                podcastsValue == 1 && gamesValue == 1) {
            showError("Выберите хотя бы один тип медиа с оценкой выше 1!");
            return false;
        }

        return true;
    }

    private void openMainScreen() {
        try {
            SessionManager.getInstance().navigateTo("Main-view.fxml", "Главная", 1200, 800,
                    (MainController controller) -> {
                        // UserData уже установлен в SessionManager
                        controller.loadData();
                    });

        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка загрузки главной страницы: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            SessionManager.getInstance().navigateTo("Preferences-view.fxml", "Выбор жанров", 700, 600,
                    (PreferencesController controller) -> controller.setUserLogin(userLogin));

        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка возврата к выбору жанров");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}