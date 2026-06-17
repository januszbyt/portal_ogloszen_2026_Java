**System Ofert Pracy**

**Specyfikacja Wymagań Funkcjonalnych**

**1\. Moduł Autoryzacji i Konta (Wspólny)**

Moduł dostępny dla wszystkich niezalogowanych użytkowników oraz posiadaczy kont (zarówno kandydatów, jak i pracodawców). Odpowiada za dostęp do systemu, bezpieczeństwo i zarządzanie tożsamością.

**A. Rejestracja i Bezpieczeństwo**

*   **Rejestracja użytkownika:** Możliwość założenia konta z obowiązkowym wyborem roli (Kandydat lub Pracodawca).
*   **Walidacja danych (Frontend):** Zanim dane trafią do bazy, interfejs w JavaFX sprawdza, czy email ma poprawny format (zawiera @ i domenę) oraz czy wymagane pola nie są puste.
*   **Wymogi siły hasła:** System wymusza, aby hasło miało minimum 8 znaków, w tym co najmniej jedną wielką literę, cyfrę i znak specjalny.
*   **Bezpieczne przechowywanie haseł:** Hasła nie są zapisywane w bazie MySQL czystym tekstem. System używa algorytmu hashującego.
*   **Unikalność konta:** System sprawdza w bazie MySQL, czy podany adres email nie jest już zarejestrowany. Jeśli jest, wyświetla odpowiedni komunikat błędu.

**B. Logowanie i Sesja**

*   **Logowanie i zarządzanie sesją:** Proces weryfikacji poświadczeń (login/email i hasło) w bazie danych MySQL, który po przyznaniu dostępu do właściwego panelu pozwala aplikacji zapamiętać tożsamość zalogowanego użytkownika podczas przełączania ekranów w JavaFX.
*   **Mechanizm odzyskiwania hasła ("Zapomniałem hasła"):** Generowanie jednorazowego kodu w przypadku utraty dostępu do konta (symulacja wysyłki emaila).

**C. Zarządzanie Profilem (Rozbudowane)**

*   **Podstawowa edycja:** Podgląd swoich danych, możliwość aktualizacji adresu email oraz zmiana hasła.
*   **Dla Kandydata:**
    *   Możliwość edycji imienia i nazwiska.
    *   Możliwość wgrania pliku CV (np. w formacie PDF), który będzie widoczny dla pracodawcy przy aplikacji.
    *   Pole na dodanie linku do profilu LinkedIn oraz GitHub.
*   **Dla Pracodawcy:**
    *   Możliwość dodania/edycji nazwy i krótkiego opisu firmy.
    *   Możliwość dodania NIP-u z automatyczną walidacją poprawności (sprawdzanie sumy kontrolnej).
*   **Zarządzanie danymi:** Przycisk "Trwale usuń konto", który wykonuje kaskadowe usunięcie w bazie danych – kasuje użytkownika oraz wszystkie jego oferty i aplikacje z bazy MySQL.

**2\. Panel Kandydata**

Funkcjonalności przeznaczone wyłącznie dla zalogowanych osób szukających pracy.

*   **Przeglądanie ofert pracy:** Wyświetlanie listy aktywnych ogłoszeń. Główne informacje na liście to: tytuł, firma, lokalizacja, wynagrodzenie.
*   **Wyszukiwanie i filtrowanie:**
    *   Wyszukiwarka tekstowa (po tytule, nazwie firmy lub słowach kluczowych).
    *   Filtry: branża/kategoria (np. IT, Sprzedaż, Budownictwo), lokalizacja, minimalne wynagrodzenie.
*   **Szczegóły oferty:** Osobny widok po kliknięciu w ofertę, pokazujący pełny opis, wymagania, benefity i datę dodania ogłoszenia.
*   **Aplikowanie na stanowisko:** Przycisk "Aplikuj" w szczegółach oferty. Aplikacja zapisuje zgłoszenie kandydata w bazie.
*   **Historia aplikacji:** Zakładka "Moje zgłoszenia", w której kandydat widzi oferty, na które wysłał CV, wraz z aktualnym statusem narzuconym przez pracodawcę (np. _Oczekująca_, _W rozpatrywaniu_, _Odrzucona_, _Zatwierdzona_).

**3\. Panel Pracodawcy**

Funkcjonalności dla firm publikujących ogłoszenia i rekrutujących pracowników.

*   **Dodawanie nowej oferty:** Formularz z polami: tytuł stanowiska, kategoria, opis, widełki płacowe (min-max), lokalizacja.
*   **Zarządzanie własnymi ofertami:** Wyświetlanie listy _tylko i wyłącznie_ własnych ogłoszeń. Możliwość ich edycji (np. poprawy literówek) oraz usuwania lub zamykania rekrutacji.
*   **Przeglądanie aplikacji:** Po kliknięciu w swoje ogłoszenie, pracodawca widzi listę kandydatów, którzy na nie zaaplikowali (wraz z ich CV i linkami, jeśli zostały podane w profilu).
*   **Zarządzanie statusem aplikacji:** Możliwość zmiany statusu zgłoszenia danego kandydata, co automatycznie odzwierciedli się w panelu kandydata.

**4\. Panel Administratora**

*   **Zarządzanie użytkownikami:** Lista wszystkich kont w systemie z możliwością ich zablokowania lub trwałego usunięcia.
*   **Moderacja ofert:** Widok wszystkich ogłoszeń w systemie z prawem do usunięcia dowolnej oferty (np. w przypadku naruszenia regulaminu lub spamu).

**Definiowanie potrzeb**

**Oczekiwania z perspektywy Kandydata:**

*   **Szybkie dotarcie do trafnych ofert:** Potrzeba intuicyjnego narzędzia z precyzyjnymi filtrami (zarobki, branża, lokalizacja), aby nie marnować czasu na przeglądanie nieadekwatnych ogłoszeń.
*   **Proces aplikacji bez barier:** Oczekiwanie maksymalnie uproszczonej formy wysyłania zgłoszeń bezpośrednio w aplikacji (z wykorzystaniem dodanego wcześniej CV), bez konieczności wypełniania długich formularzy zewnętrznych.
*   **Transparentność procesu rekrutacji:** Potrzeba informacji zwrotnej. Kandydat chce mieć stały podgląd na to, co dzieje się z jego aplikacją (poprzez widoczne zmiany statusów nadawane przez pracodawcę).
*   **Kontrola nad własnymi danymi:** Możliwość swobodnego zarządzania swoim profilem i aktualizowania dokumentów aplikacyjnych.

**Oczekiwania z perspektywy Pracodawcy:**

*   **Efektywna i szybka publikacja:** Oczekiwanie prostego kreatora ogłoszeń, który pozwala sprawnie sformułować wymagania, opisać stanowisko i określić widełki płacowe.
*   **Scentralizowana baza aplikacji:** Potrzeba gromadzenia wszystkich odpowiedzi na dane ogłoszenie w jednym uporządkowanym miejscu, co eliminuje chaos związany z klasycznym odbieraniem CV na skrzynkę e-mail.
*   **Sprawna selekcja i organizacja:** Możliwość wygodnego przeglądania profili kandydatów oraz kategoryzowania ich poprzez zmianę statusów (np. odrzucenie lub zaproszenie do kolejnego etapu), co pozwala utrzymać porządek w trwających rekrutacjach.
*   **Elastyczność w zarządzaniu ofertami:** Potrzeba pełnej kontroli nad cyklem życia ogłoszenia – od możliwości szybkiej edycji po natychmiastowe zdjęcie oferty z tablicy w momencie obsadzenia stanowiska.

**Zasady działania**

**1\. Reguły dostępu i ról (Access Control)**

*   **Niezmienność przypisanej roli:** Każde konto jest jednoznacznie i trwale przypisane do jednej z dwóch ról (Kandydat lub Pracodawca) w momencie rejestracji. Użytkownik nie może samodzielnie zmienić swojej roli w systemie.
*   **Wymóg logowania:** Interakcja z systemem (przeglądanie, dodawanie, aplikowanie) wymaga aktywnej sesji. Użytkownik niezalogowany nie może pominąć ekranu startowego.

**2\. Ograniczenia dla Kandydata**

*   **Unikalność aplikacji:** Kandydat może zaaplikować na konkretną ofertę pracy tylko raz. Próba ponownego wysłania zgłoszenia na to samo ogłoszenie zostanie zablokowana przez system.
*   **Izolacja danych:** Kandydat ma wgląd wyłącznie w historię własnych aplikacji.
*   **Brak możliwości zarządzania ofertami:** Kandydat nie posiada interfejsu ani uprawnień do tworzenia, edycji czy usuwania ofert pracy.

**3\. Ograniczenia dla Pracodawcy**

*   **Własność ogłoszeń:** Pracodawca posiada pełne prawa (edycja, zamykanie rekrutacji) tylko i wyłącznie do ofert utworzonych ze swojego konta.
*   **Dostęp do aplikacji:** Pracodawca widzi zgłoszenia i dane osobowe kandydatów tylko w obrębie własnych ogłoszeń. Nie ma dostępu do ogólnej bazy kandydatów w systemie.
*   **Ograniczenia aplikacyjne:** Konto pracodawcy nie posiada technicznej możliwości zaaplikowania na jakąkolwiek ofertę pracy (brak przycisku "Aplikuj").

**4\. Reguły integralności danych**

*   **Wymagane pola formularza:** Próba dodania nowej oferty bez wypełnienia kluczowych pól (tytuł, opis, przedział wynagrodzenia) jest blokowana na poziomie warstwy prezentacji i nie dociera do bazy danych.
*   **Kaskadowe usuwanie danych:** Usunięcie konta przez Pracodawcę powoduje bezpowrotne usunięcie z bazy wszystkich jego ofert pracy oraz powiązanych z nimi aplikacji.
*   Usunięcie konta przez Kandydata bezpowrotnie kasuje jego profil oraz wszystkie aplikacje wysłane na oferty pracodawców.
*   Trwałe usunięcie konkretnej oferty przez pracodawcę automatycznie kasuje z bazy wszystkie powiązane z nią zgłoszenia kandydatów.

**Skrótowy opis: Interakcje i Przypadki Użycia**

**Moduł: Autoryzacja i Zarządzanie Kontem (Wspólne)**

*   Rejestracja nowego użytkownika (założenie konta z wyborem roli: Kandydat lub Pracodawca).
*   Logowanie do systemu (weryfikacja poświadczeń i nadanie dostępu do odpowiedniego panelu).
*   Wylogowanie z systemu (zakończenie sesji użytkownika).
*   Edycja danych profilowych (zmiana hasła, nazwy firmy, imienia i nazwiska).
*   Trwałe usunięcie konta (kaskadowe usunięcie wszystkich danych użytkownika z bazy).

**Moduł: Pracodawca**

*   Dodawanie nowej oferty pracy (wypełnienie formularza i publikacja ogłoszenia).
*   Przeglądanie listy własnych ofert (wyświetlenie ogłoszeń przypisanych tylko do zalogowanego pracodawcy).
*   Edycja opublikowanej oferty pracy (poprawa błędów lub aktualizacja wymagań).
*   Zamykanie lub usuwanie oferty pracy (zakończenie rekrutacji lub trwałe wykasowanie ogłoszenia).
*   Przeglądanie listy zgłoszeń (wyświetlenie kandydatów, którzy zaaplikowali na konkretną ofertę).
*   Zmiana statusu aplikacji kandydata (np. na "Odrzucony", "W toku", co aktualizuje się w panelu kandydata).

**Moduł: Kandydat**

*   Przeglądanie dostępnych ofert pracy (wyświetlenie głównej listy wszystkich aktywnych ogłoszeń).
*   Wyszukiwanie i filtrowanie ofert (użycie paska wyszukiwania i filtrów np. lokalizacja, kategoria, wynagrodzenie).
*   Wyświetlanie szczegółów ogłoszenia (otwarcie pełnego opisu wybranej oferty pracy).
*   Aplikowanie na ofertę pracy (wysłanie zgłoszenia na wybrane stanowisko jednym kliknięciem).
*   Przeglądanie historii własnych aplikacji (sprawdzanie statusów wysłanych zgłoszeń).

**Moduł: Administrator**

*   Przeglądanie listy wszystkich użytkowników.
*   Zarządzanie użytkownikami (blokowanie lub usuwanie kont).
*   Moderacja ogłoszeń (usuwanie ofert np. będących spamem).