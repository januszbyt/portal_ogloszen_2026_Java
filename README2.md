# Zespołowy Projekt JavaFX - Grupa 4 (Poradnik)

Witajcie w projekcie! Poniżej znajdziecie kompletną "ściągę" jak pracować z JavaFX, jeśli dopiero zaczynacie.

---

## 🚀 1. Jak uruchomić projekt?

W folderze głównym znajduje się skrypt `javafx-run.bat`.
- **Jak?** Kliknij go dwukrotnie lub wpisz w terminalu: `.\javafx-run.bat`.
- **Co się dzieje?** Skrypt używa lokalnego Mavena (`tools/maven`), pobiera biblioteki i uruchamia okno aplikacji.

---

## 📁 2. Gdzie są pliki?

- `src/main/java/org/example/` -> Tu tworzymy pliki **Logiki (Java)**.
- `src/main/resources/org/example/` -> Tu tworzymy pliki **Wyglądu (FXML)** i **Style (CSS)**.
- `pom.xml` -> Tu dodajemy nowe biblioteki (np. do bazy danych).
- `tools/` -> Tu macie instalator **Scene Buildera** i inne narzędzia.

---

## 🎨 3. Tworzenie nowej strony (Krok po kroku)

### Krok A: Stwórz wygląd (FXML)
1. Skopiuj plik `primary.fxml` i nazwij go np. `moje_okno.fxml`.
2. Otwórz go w **Scene Builderze** (Prawy przycisk w VS Code -> Open in Scene Builder).
3. Zaprojektuj wygląd (przeciągaj przyciski, etykiety).
4. **Bardzo ważne:** W lewym dolnym rogu Scene Buildera (sekcja `Controller`) wpisz: `org.example.MojKontroler`.

### Krok B: Stwórz logikę (Java)
1. Stwórz nową klasę w Javie: `MojKontroler.java`.
2. Jeśli chcesz obsłużyć przycisk, nadaj mu w Scene Builderze ID (zakładka `Code` -> `fx:id`) oraz akcję (`On Action`).
3. W Javie napisz:
   ```java
   @FXML
   private void nazwaAkcji() {
       System.out.println("Kliknięto!");
   }
   ```

### Krok C: Przełączanie się między oknami
Aby po kliknięciu przycisku przejść do innej strony, wpisz w metodzie:
```java
App.setRoot("moje_okno"); // nazwa pliku fxml bez końcówki
```

---

## 🖌️ 4. Stylizowanie (CSS)

1. Stwórz plik `style.css` w folderze `resources`.
2. Przykładowy styl:
   ```css
   .button { -fx-background-color: #3498db; -fx-text-fill: white; }
   ```
3. Podepnij go w Scene Builderze w zakładce `Properties` -> `Stylesheets`.

---

## 💡 5. Złote rady dla początkujących

- **Znikający przycisk?** Sprawdź, czy w pliku FXML główny kontener (np. VBox) nie jest za mały.
- **Błąd "Controller not found"?** Upewnij się, że w Scene Builderze w sekcji `Controller` wpisałeś pełną nazwę: `org.example.NazwaKlasy`.
- **Błąd "OnAction not found"?** Sprawdź, czy metoda w Javie ma adnotację `@FXML` i czy nazwa w Scene Builderze jest identyczna.
- **Odświeżanie zmian**: Jeśli zmienisz coś w kodzie, musisz wyłączyć aplikację i uruchomić `javafx-run.bat` ponownie.

---

## 🛠️ Narzędzia pomocnicze (Folder `tools/`)
- Zainstalujcie `SceneBuilder-Installer.msi`.
- Jeśli VS Code nie podświetla składni JavaFX, zainstalujcie plik `.vsix` (przeciągając go do okna rozszerzeń w VS Code).
