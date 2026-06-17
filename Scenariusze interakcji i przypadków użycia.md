**1\. Moduł: Autoryzacja i Zarządzanie Kontem (Wspólne)**

**1.1 Rejestracja nowego użytkownika**

*   **Aktor:** Niezalogowany użytkownik (Gość)
*   **Warunek początkowy:** Użytkownik znajduje się na stronie rejestracji.
*   **Główny scenariusz:**
    1.  Użytkownik wybiera rolę: Kandydat lub Pracodawca.
    2.  Wypełnia formularz rejestracyjny (e-mail, hasło, oraz podstawowe dane zależne od roli, zmieniane dynamicznie: imię/nazwisko dla kandydata, nazwa firmy dla pracodawcy).
    3.  System weryfikuje unikalność adresu e-mail i poprawność danych.
    4.  System tworzy nowe konto i wysyła e-mail potwierdzający.
*   **Warunek końcowy:** Konto zostaje utworzone w bazie danych, użytkownik może się zalogować.

**1.2 Logowanie do systemu**

*   **Aktor:** Zarejestrowany użytkownik
*   **Główny scenariusz:**
    1.  Użytkownik wprowadza e-mail i hasło.
    2.  System weryfikuje poświadczenia w bazie danych.
    3.  Po pozytywnej weryfikacji system sprawdza rolę użytkownika (Kandydat, Pracodawca, Administrator).
    4.  System inicjuje sesję i przekierowuje użytkownika do dedykowanego panelu.
*   **Scenariusz alternatywny:** W przypadku błędnych danych, system wyświetla komunikat o błędzie i prosi o ponowną próbę.

**1.3 Wylogowanie z systemu**

*   **Aktor:** Zalogowany użytkownik
*   **Główny scenariusz:**
    1.  Użytkownik klika przycisk "Wyloguj".
    2.  System niszczy aktywną sesję.
    3.  Użytkownik zostaje przekierowany na stronę główną.

**1.4 Edycja danych profilowych**

*   **Aktor:** Zalogowany użytkownik (Kandydat / Pracodawca)
*   **Główny scenariusz:**
    1.  Użytkownik wchodzi w zakładkę "Mój profil".
    2.  Wprowadza nowe dane (np. zmienia nazwę firmy, imię i nazwisko) lub podaje obecne i nowe hasło w celu jego zmiany.
    3.  Zapisuje zmiany.
    4.  System waliduje dane i aktualizuje wpis w bazie.
*   **Warunek końcowy:** Zaktualizowane dane są natychmiast widoczne w systemie.

**1.5 Trwałe usunięcie konta**

*   **Aktor:** Zalogowany użytkownik
*   **Główny scenariusz:**
    1.  Użytkownik wchodzi w zakładkę "Mój profil".
    2.  Użytkownik wybiera opcję "Usuń konto".
    3.  System prosi o potwierdzenie akcji poprzez wpisanie hasła.
    4.  Po potwierdzeniu, system wykonuje kaskadowe usunięcie danych.
*   **Warunek końcowy:** Użytkownik zostaje wylogowany, a jego dane bezpowrotnie znikają z bazy.

**1.6 Odzyskiwanie hasła ("Zapomniałem hasła")**

*   **Aktor:** Niezalogowany użytkownik (Kandydat / Pracodawca)
*   **Warunek początkowy:** Użytkownik znajduje się na ekranie logowania, ale nie pamięta swojego hasła.
*   **Główny scenariusz:**
    1.  Użytkownik klika "Zapomniałem hasła" na ekranie logowania.
    2.  Przechodzi na osobny formularz.
    3.  Wprowadza adres e-mail powiązany ze swoim kontem.
    4.  System weryfikuje, czy podany e-mail istnieje w bazie danych.
    5.  System generuje jednorazowy kod autoryzacyjny i symuluje wysłanie wiadomości e-mail (wyświetla wygenerowany kod na ekranie).
    6.  Użytkownik wprowadza otrzymany kod na nowym ekranie i definiuje nowe hasło (zgodne z wymogami bezpieczeństwa), po czym zatwierdza zmianę.
    7.  System hashuję nowe hasło, nadpisuje stare w bazie danych.
    8.  Powrót do okna logowania.

**2\. Moduł: Pracodawca**

**2.1 Dodawanie nowej oferty pracy**

*   **Aktor:** Zalogowany Pracodawca
*   **Główny scenariusz:**
    1.  Pracodawca klika przycisk "Dodaj ofertę".
    2.  Wypełnia formularz (tytuł stanowiska, opis, wymagania, widełki płacowe, kategoria, lokalizacja).
    3.  Klika "Publikuj".
    4.  System zapisuje ofertę w bazie ze statusem "Aktywna".
*   **Warunek końcowy:** Oferta jest widoczna na głównej liście ogłoszeń dla Kandydatów.

**2.2 Przeglądanie listy własnych ofert**

*   **Aktor:** Zalogowany Pracodawca
*   **Główny scenariusz:**
    1.  Pracodawca przechodzi do zakładki "Moje oferty".
    2.  System pobiera z bazy i wyświetla listę ogłoszeń przypisanych tylko do ID tego pracodawcy.
    3.  Pracodawca widzi podstawowe statystyki (np. liczba aplikacji, status oferty).

**2.3 Edycja opublikowanej oferty pracy**

*   **Aktor:** Zalogowany Pracodawca
*   **Główny scenariusz:**
    1.  Pracodawca wybiera aktywną ofertę z listy własnych ogłoszeń i klika "Edytuj".
    2.  Wprowadza poprawki w formularzu.
    3.  Zapisuje zmiany.
*   **Warunek końcowy:** Zaktualizowane informacje natychmiast odzwierciedlają się na stronie widoku oferty.

**2.4 Zamykanie lub usuwanie oferty pracy**

*   **Aktor:** Zalogowany Pracodawca
*   **Główny scenariusz:**
    1.  Pracodawca przechodzi do zakładki "Moje oferty".
    2.  Pracodawca wybiera ofertę i klika "Zakończ rekrutację" (oferta staje się nieaktywna, ale zostaje w archiwum) lub "Usuń" (oferta znika całkowicie).
    3.  System prosi o potwierdzenie.
*   **Warunek końcowy:** Oferta znika z publicznej listy dostępnych ogłoszeń.

**2.5 Przeglądanie listy zgłoszeń (Aplikacji)**

*   **Aktor:** Zalogowany Pracodawca
*   **Główny scenariusz:**
    1.  Pracodawca przechodzi do zakładki "Moje oferty".
    2.  Pracodawca klika wybraną ofertę w swoim panelu.
    3.  Przechodzi do widoku "Kandydaci".
    4.  System wyświetla listę osób, które zaaplikowały, wraz z ich podstawowymi danymi i załączonymi dokumentami (np. CV).

**2.6 Zmiana statusu aplikacji kandydata**

*   **Aktor:** Zalogowany Pracodawca
*   **Główny scenariusz:**
    1.  W widoku zgłoszeń, pracodawca wybiera kandydata.
    2.  Rozwija menu statusu i zmienia go (np. "Nowy" -> "W toku" lub "Odrzucony").
    3.  System aktualizuje status w bazie danych.
*   **Warunek końcowy:** Zmieniony status jest natychmiast widoczny dla kandydata w jego prywatnym panelu.

**3\. Moduł: Kandydat**

**3.1 Przeglądanie dostępnych ofert pracy**

*   **Aktor:** Kandydat
*   **Główny scenariusz:**
    1.  Użytkownik wchodzi na stronę główną portalu.
    2.  System wyświetla podzieloną na strony listę wszystkich aktywnych ogłoszeń dodanych przez pracodawców.

**3.2 Wyszukiwanie i filtrowanie ofert**

*   **Aktor:** Kandydat
*   **Główny scenariusz:**
    1.  Użytkownik wpisuje słowo kluczowe w pasek wyszukiwania.
    2.  Zaznacza dodatkowe filtry (np. miasto, branża, minimalne wynagrodzenie).
    3.  Klika "Szukaj".
    4.  System zawęża listę wyświetlanych ogłoszeń na podstawie podanych kryteriów.

**3.3 Wyświetlanie szczegółów ogłoszenia**

*   **Aktor:** Kandydat
*   **Główny scenariusz:**
    1.  Użytkownik klika w interesujący go kafelek oferty.
    2.  Otwiera się nowa strona z pełnym opisem, wymaganiami, informacjami o firmie i przyciskiem "Aplikuj".

**3.4 Aplikowanie na ofertę pracy**

*   **Aktor:** Kandydat
*   **Warunek początkowy:** Kandydat ogląda szczegóły oferty i nie złożył jeszcze aplikacji na to stanowisko.
*   **Główny scenariusz:**
    1.  Kandydat klika przycisk "Aplikuj"
    2.  System pobiera dane z profilu kandydata (CV, dane kontaktowe).
    3.  System tworzy powiązanie między ID Kandydata a ID Oferty ze statusem domyślnym "Przesłano".
*   **Warunek końcowy:** Aplikacja trafia do panelu Pracodawcy. Przycisk "Aplikuj" dla tego kandydata zmienia się na "Aplikowano".

**3.5 Przeglądanie historii własnych aplikacji**

*   **Aktor:** Kandydat
*   **Główny scenariusz:**
    1.  Kandydat wchodzi do zakładki "Moje aplikacje".
    2.  System wyświetla listę ofert, na które wysłano zgłoszenia.
    3.  Kandydat widzi aktualny status dla każdej aplikacji (np. "Wysłana", "W toku", "Odrzucony").

**3.6 Wycofanie przesłanej aplikacji**

*   **Aktor:** Kandydat
*   **Warunek początkowy:** Kandydat wysłał już zgłoszenie na ofertę (aplikacja widnieje na liście w zakładce "Moje aplikacje").
*   **Główny scenariusz:**
    1.  Kandydat wchodzi w zakładkę "Moje aplikacje".
    2.  Wybiera wysłane zgłoszenie i klika przycisk "Wycofaj aplikację".
    3.  System prosi o potwierdzenie tej decyzji (z informacją, że akcja jest nieodwracalna).
    4.  Po potwierdzeniu, system całkowicie usuwa rekord powiązania.
*   **Warunek końcowy:** Aplikacja znika z listy kandydatów w panelu Pracodawcy, a Kandydat odzyskuje możliwość ponownego zaaplikowania na tę samą ofertę.

**4\. Moduł: Administrator**

**4.1 Przeglądanie listy wszystkich użytkowników**

*   **Aktor:** Administrator
*   **Główny scenariusz:**
    1.  Admin przechodzi do sekcji "Zarządzanie Użytkownikami" w panelu administracyjnym.
    2.  System wyświetla listę ze wszystkimi zarejestrowanymi użytkownikami (zarówno Kandydatami, jak i Pracodawcami) wraz z ich danymi i datami rejestracji.

**4.2 Zarządzanie użytkownikami (Blokowanie/Usuwanie)**

*   **Aktor:** Administrator
*   **Główny scenariusz:**
    1.  Admin przechodzi do sekcji "Zarządzanie Użytkownikami" w panelu administracyjnym.
    2.  Admin wyszukuje użytkownika łamiącego regulamin.
    3.  Z menu akcji wybiera "Zablokuj" (blokuje możliwość logowania, ale dane zostają) lub "Usuń" (trwałe usunięcie konta z bazy).
    4.  System prosi o potwierdzenie akcji.
*   **Warunek końcowy:** Użytkownik traci dostęp do systemu.

**4.3 Odblokowanie konta użytkownika**

*   **Aktor:** Administrator
*   **Warunek początkowy:** Konto użytkownika (Kandydata lub Pracodawcy) ma status "Zablokowane".
*   **Główny scenariusz:**
    1.  Admin przechodzi do sekcji "Zarządzanie Użytkownikami" w panelu administracyjnym.
    2.  Wybiera odpowiednie konto i z menu akcji wybiera "Odblokuj".
    3.  System zmienia status konta w bazie danych.
*   **Warunek końcowy:** Użytkownik odzyskuje możliwość logowania się do systemu.

**4.4 Moderacja ogłoszeń**

*   **Aktor:** Administrator
*   **Główny scenariusz:**
    1.  Admin przegląda listę wszystkich opublikowanych ogłoszeń o pracę.
    2.  Wybiera ofertę i klika "Usuń ofertę".
    3.  System trwale usuwa ofertę z widoku publicznego i panelu pracodawcy.