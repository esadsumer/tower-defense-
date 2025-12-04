# Tower Defense Oyunu - Sınıf Diyagramı

## Genel Bakış

Bu proje, Antik Mısır temalı bir tower defense oyunudur. OOP prensipleri (Encapsulation, Inheritance, Polymorphism, Abstraction) kullanılarak geliştirilmiştir.

## Sınıf Yapısı

### 1. Enemy Hiyerarşisi (Düşman Sınıfları)

#### Enemy (Abstract Base Class)
- **Amaç**: Tüm düşman türleri için temel sınıf
- **Özellikler**: Can, hız, zırh, pozisyon, ödül
- **Metodlar**: Hasar alma, yavaşlatma efekti, kaleye ulaşma kontrolü

#### StandardEnemy (Goblin)
- Enemy'den türetilir
- Normal zemin düşmanı
- Düşük can, orta hız, zırh yok

#### ArmoredEnemy (Ejderha)
- Enemy'den türetilir
- Zırhlı zemin düşmanı
- Yüksek can, düşük hız, yüksek zırh

#### FlyingEnemy (Akbaba)
- Enemy'den türetilir
- Uçan düşman
- Orta can, yüksek hız, zırh yok

### 2. Tower Hiyerarşisi (Kule Sınıfları)

#### Tower (Abstract Base Class)
- **Amaç**: Tüm kule türleri için temel sınıf
- **Özellikler**: Pozisyon, menzil, hasar, atış hızı, maliyet
- **Metodlar**: Ateş etme, menzil kontrolü, hedef bulma

#### ArcherTower (Okçu Kulesi)
- Tower'den türetilir
- Tek hedef, hızlı atış
- Zırhlı düşmanlara %50 az hasar

#### CannonTower (Topçu Kulesi)
- Tower'den türetilir
- Alan hasarı (splash damage)
- Uçan düşmanları hedefleyemez

#### IceTower (Buz Kulesi)
- Tower'den türetilir
- Düşmanları yavaşlatır
- Hasar + yavaşlatma efekti

### 3. Path Sınıfı

#### Path
- Düşmanların takip ettiği yol
- Waypoint'ler içerir
- İç sınıf: Point (x, y koordinatları)

### 4. Game Engine

#### GameEngine
- **Amaç**: Oyun mantığını yönetir
- **Sorumluluklar**:
  - Düşman hareketi
  - Kule ateşleme
  - Hasar hesaplamaları
  - Dalga yönetimi
  - Oyun durumu kontrolü
- **İlişkiler**: Enemy, Tower, Path listelerini yönetir

### 5. GUI Sınıfları

#### GameGUI
- **Amaç**: Oyun görselleştirmesi
- **Sorumluluklar**:
  - Oyun ekranını çizme
  - Kullanıcı etkileşimleri
  - Kule yerleştirme
  - UI elementleri

#### MainMenu
- **Amaç**: Ana menü ekranı
- **Özellikler**: Başlat ve Çıkış butonları
- **Interface**: MainMenuListener

#### MainMenuListener (Interface)
- onStartGame()
- onExit()

### 6. Ana Sınıf

#### SpaceColonyDefense
- **Amaç**: Uygulama giriş noktası
- **Sorumluluklar**:
  - Tüm bileşenleri koordine eder
  - Pencere yönetimi
  - Menü ve oyun ekranları arası geçiş

## İlişkiler

### Kalıtım (Inheritance)
- `Enemy` → `StandardEnemy`, `ArmoredEnemy`, `FlyingEnemy`
- `Tower` → `ArcherTower`, `CannonTower`, `IceTower`

### Kompozisyon (Composition)
- `Path` contains `Point` (inner class)
- `GameEngine` has `List<Enemy>`, `List<Tower>`, `Path`
- `SpaceColonyDefense` has `GameEngine`, `GameGUI`, `MainMenu`

### Bağımlılık (Dependency)
- `GameGUI` uses `GameEngine`
- `Tower` uses `Enemy` (ateş etme)
- `ArcherTower` checks `ArmoredEnemy` instanceof

### Interface
- `MainMenu` implements `MainMenuListener`

## OOP Prensipleri

1. **Encapsulation**: Tüm sınıflarda private/protected alanlar ve getter/setter metodları
2. **Inheritance**: Enemy ve Tower hiyerarşileri
3. **Polymorphism**: Abstract metodlar (fire(), getEnemyType(), getTowerType())
4. **Abstraction**: Enemy ve Tower abstract sınıfları

## Dosya Yapısı

```
Tower Defense/
├── Enemy.java (Abstract)
├── StandardEnemy.java
├── ArmoredEnemy.java
├── FlyingEnemy.java
├── Tower.java (Abstract)
├── ArcherTower.java
├── CannonTower.java
├── IceTower.java
├── Path.java
├── GameEngine.java
├── GameGUI.java
├── MainMenu.java
└── SpaceColonyDefense.java
```

## PlantUML Diyagramı

PlantUML formatındaki detaylı sınıf diyagramı için `ClassDiagram.puml` dosyasına bakın.

Bu dosyayı PlantUML ile görselleştirmek için:
- Online: http://www.plantuml.com/plantuml/uml/
- VS Code: PlantUML extension
- IntelliJ IDEA: PlantUML plugin






