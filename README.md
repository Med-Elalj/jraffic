# Traffic Simulator 🚦


> An AI-driven traffic intersection simulator with intelligent traffic management, real-time physics, and immersive visuals. Experience efficient urban mobility through interactive simulation!

## ✨ Features

### 🚦 Intelligent Traffic Management
- **Adaptive Traffic Lights**: AI-powered system that dynamically adjusts light timing based on real-time traffic flow
- **Priority Logic**: Instant green lights when only one direction has vehicles, optimizing traffic efficiency
- **Fairness Algorithm**: Prevents starvation with round-robin scheduling and wait-time tracking
- **Collision-Free Zones**: Smart intersection clearing ensures safe vehicle passage

### 🚗 Advanced Vehicle Simulation
- **Realistic Physics**: Collision detection, safe following distances, and smooth movement
- **Diverse Vehicle Types**: Blue, Yellow, and Brown cars with unique turning behaviors
- **Sprite-Based Rendering**: High-quality vehicle sprites with directional animations
- **Dynamic Spawning**: Intelligent spawn limits prevent overcrowding

### 🎮 Immersive User Experience
- **Interactive Controls**: Intuitive keyboard controls for spawning vehicles
- **Real-Time Rendering**: Smooth 60 FPS gameplay with responsive graphics
- **Cross-Platform**: Runs on Windows, macOS, Linux, and more

### 🛠️ Technical Excellence
- **Pure Java Implementation**: No external dependencies, fully portable
- **Modular Architecture**: Clean separation of concerns for easy maintenance
- **Performance Optimized**: Efficient algorithms for large-scale simulations
- **Extensible Design**: Easy to add new features and vehicle types

## 🎯 Demo

![Traffic Simulator Demo](demo.gif)

*Watch vehicles navigate the intersection with intelligent traffic lights adapting to flow*

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 17 or higher
- **No external dependencies** required

### Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/jraffic.git
   cd jraffic
   ```

2. **Option 1: Use the run script** (recommended):
   ```bash
   ./run.sh
   ```

3. **Option 2: Manual build**:
   ```bash
   cd src
   javac -d ../build *.java
   java -cp ../build Main
   ```

## 🎮 Controls

| Key | Action |
|-----|--------|
| ↑ | Spawn vehicle from South |
| ↓ | Spawn vehicle from North |
| ← | Spawn vehicle from West |
| → | Spawn vehicle from East |
| R | Spawn random vehicle |
| ESC | Exit simulation |

## 🏗️ Architecture

### Source Code Structure
```
src/
├── Main.java              # Main application class with GUI and game loop
├── Vehicle.java           # Vehicle physics, movement, and behavior
├── TrafficSystem.java     # AI traffic controller and light management
└── MovementDirection.java # Enum for directional movement
```

## 🧠 AI Traffic System

The core intelligence behind the Traffic Simulator:

### 🔄 Adaptive Light Cycling
- **Round-Robin**: Fair distribution of green time across all directions
- **Dynamic Timing**: 180-frame phases with automatic adjustment
- **Starvation Prevention**: 3-second maximum wait time triggers priority

### 🎯 Smart Priority Logic
```java
// Example priority check
if (onlyOneDirectionHasVehicles() && intersectionIsClear()) {
    switchToDirectionImmediately();
}
```

### 📊 Real-Time Analytics
- Vehicle counting per direction
- Wait time tracking
- Intersection occupancy monitoring
- Collision prediction and prevention

## 🎨 Visual Design

### Color Palette
- **Background**: Deep blue (#05080F)
- **Roads**: Modern asphalt gray (#182028)
- **Accents**: Electric blue (#0080FF) and yellow (#FFFF20)
- **Traffic Lights**: Glowing red/green with aura effects

### Vehicle Sprites
- **Blue Cars**: Straight-through traffic
- **Yellow Cars**: Right-turn specialists
- **Brown Cars**: Left-turn experts
- **Directional Rendering**: 4 orientations per color

## 📈 Performance

- **60 FPS**: Smooth animation on modern hardware
- **Low Memory**: Efficient sprite management
- **Scalable**: Handles up to 28 vehicles simultaneously
- **Cross-Platform**: Optimized for different architectures

## 🤝 Contributing

We welcome contributions! Here's how to get started:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** your changes: `git commit -m 'Add amazing feature'`
4. **Push** to the branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Development Setup
```bash
# Java development
cd java
javac *.java
java Main
```

### Code Style
- **Java**: Standard Oracle Java conventions
- **Commits**: Use conventional commit format

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **OpenJDK**: For reliable Java runtime
- **Community**: For inspiration and feedback

## 🚀 Future Roadmap

- [ ] **Multi-Intersection Networks**: Connect multiple intersections
- [ ] **Pedestrian Simulation**: Add crosswalks and pedestrian traffic
- [ ] **Weather Effects**: Rain, fog, and lighting changes
- [ ] **Traffic Analytics**: Detailed statistics and reporting
- [ ] **Mobile Version**: Touch controls for smartphones
- [ ] **VR Support**: Immersive 3D traffic control


---

<div align="center">

**Built with ❤️ for the future of smart cities**

⭐ Star this repo if you find it useful!

[🌐 Live Demo](demo-link) | [📖 Documentation](docs/) | [🎮 Play Now](play-link)

</div>
