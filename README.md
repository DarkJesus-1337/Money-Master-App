# ![MoneyMaster](app/src/main/res/mipmap-xhdpi/ic_launcher_foreground.webp) 
# MoneyMaster

Eine moderne Android-Anwendung zur umfassenden Verwaltung persönlicher Finanzen, entwickelt mit Kotlin und Jetpack Compose.

## 🌟 Hauptfunktionen

### 📊 Erweiterte Finanzanalyse
- **Echtzeit-Dashboard** mit detaillierter Finanzübersicht
- **Interaktive Statistiken** mit mehreren Analyseebenen:
  - Übersichts-Tab mit monatlichen Zusammenfassungen
  - Kategorien-Tab mit detaillierter Aufschlüsselung nach Ausgabenkategorien
  - Trend-Tab mit historischen Entwicklungen und Prognosen
- **Visuelle Diagramme** für bessere Datenvisualisierung
- **Saldo-Berechnung** mit Einnahmen-Ausgaben-Analyse

### 💳 Intelligente Transaktionsverwaltung
- **Erweiterte Transaktionserstellung** mit Betrag, Titel, Beschreibung und Kategorie
- **Vollständige CRUD-Operationen** (Erstellen, Bearbeiten, Löschen)
- **Flexible Kategorisierung** mit farbcodierten Kategorien
- **Präzise Datumsverwaltung** für chronologische Aufzeichnungen
- **Ausgaben- und Einnahmentracking** mit automatischer Saldoaktualisierung

### 🏷️ Dynamisches Kategoriensystem
- **Vordefinierte Kategorien** für verschiedene Ausgabentypen (Lebensmittel, Transport, Unterhaltung, etc.)
- **Benutzerdefinierte Kategorien** mit individuellen Farben und Icons
- **Kategoriestatistiken** mit detaillierter Analyse pro Kategorie
- **Farbcodierung** für bessere visuelle Übersicht

### 💰 Budget-Management
- **Budgeterstellung** für verschiedene Kategorien
- **Ausgabenüberwachung** mit Echtzeit-Tracking
- **Budgetperioden** (monatlich, wöchentlich)
- **Warnsystem** bei Budgetüberschreitungen

### 📈 Kryptowährungsportfolio
- **Crypto-Assets-Tracking** mit aktuellen Marktdaten
- **Marktkapitalisierung und Volumen** für verschiedene Kryptowährungen
- **Preishistorie** und Performance-Tracking
- **Rank und Marktdaten** für beliebte Kryptowährungen (Bitcoin, Ethereum, etc.)

### 🧾 OCR-Belegscanner
- **Automatische Belegerkennung** mittels OCR-Technologie
- **Textextraktion** aus Kassenbons und Rechnungen
- **Intelligente Datenverarbeitung** zur automatischen Transaktionserstellung
- **Bildkomprimierung** für optimale Verarbeitung

### 🚀 Moderne App-Features

#### **Automatisches Update-System**
- Updates werden direkt in der App angeboten
- Bereitstellung über **GitHub Releases**
- Automatischer Download und Installation
- Changelog-Integration für Transparenz

#### **Personalisierte Einstellungen**
- **Dark Mode** für bessere Benutzererfahrung
- **Persönliche Dateneinstellungen** (Einkommen, fixe Kosten)
- **App-Konfiguration** und Präferenzen
- **Mehrsprachige Unterstützung** (Deutsch, Englisch)

#### **Performance & Usability**
- **Responsive Design** mit Jetpack Compose
- **Offline-Funktionalität** für alle Grundfunktionen
- **Schnelle Navigation** mit Bottom Navigation
- **Intuitive Benutzeroberfläche** mit Material Design

## 📱 Download

Die aktuelle APK kann [hier heruntergeladen werden](https://github.com/DarkJesus-1337/Money-Master-App/releases).

## 🎯 Projektpräsentation

**[📊 Abschlusspräsentation ansehen](https://darkjesus-1337.github.io/Money-Master-App/presentation/abschlussprasentation.html)**

Eine interaktive HTML-Präsentation mit detaillierter Projektübersicht, technischen Details, Demo-Screenshots und Entwicklungsherausforderungen.

## 🛠️ Technische Details

### Tech Stack
- **Kotlin** – Moderne Programmiersprache für Android
- **Jetpack Compose** – Deklaratives UI-Framework
- **Android Architecture Components**:
  - ViewModel & StateFlow für Zustandsverwaltung
  - Room Database für lokale Datenspeicherung
  - Coroutines für asynchrone Operationen
- **MVVM Architecture** – Saubere Architektur mit klarer Trennung
- **Repository Pattern** – Abstrahierte Datenzugriffschicht
- **Retrofit** – HTTP-Client für API-Kommunikation
- **OCR-Integration** – Für automatische Belegerkennung
- **Chart Libraries** – Für Datenvisualisierung

### Architektur-Highlights
- **Modulare Struktur** mit klarer Komponentenaufteilung
- **Dependency Injection** für bessere Testbarkeit
- **Reactive Programming** mit StateFlow und Coroutines
- **Offline-First Design** mit lokaler Datenspeicherung
- **Error Handling** mit umfassender Fehlerbehandlung

## 📊 App-Struktur

```
📱 MoneyMaster
├── 🏠 Dashboard (Finanzübersicht)
├── 💳 Transaktionen (Verwaltung & Historie)
├── 📊 Statistiken 
│   ├── Übersicht
│   ├── Kategorien
│   ├── Trends
│   └── Krypto
├── 💰 Budgets (Planung & Kontrolle)
├── 🧾 Scanner (OCR-Belegerkennung)
└── ⚙️ Einstellungen
```

## 📄 Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert – siehe [LICENSE](LICENSE.md) Datei für Details.

## 👥 Mitwirkende

- **DarkJesus-1337** – Hauptentwickler

## ❓ Unterstützung

Bei Fragen oder Problemen erstelle bitte ein Issue im GitHub-Repository.

---

**Entwickelt mit ❤️ für eine intelligente und umfassende Finanzverwaltung**
