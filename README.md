# DynamicVibrator — Dynamic Vibration Controller for Xperia

### Introduction

A small Android utility to restore and control **Dynamic Vibration** on Sony Xperia devices.
This project specifically targets Xperia devices running ported ROMs (MIUI, Flyme, HyperOS) or GSI, where Dynamic Vibration control interface is often missing or inaccessible.

### Prerequisites

* **Device Support**: Your device must natively support Dynamic Vibration in the official stock ROM.
* **Audio HAL**: The ROM must be based on Sony's original Audio HAL.  
  AOSP / generic Audio HALs are **not supported**.

### Features

* **QS Tile Support**: Quickly toggle the feature via the Quick Settings panel.
* **Intensity Levels**: Cycle between Low, Medium, and High.
* **Long Press Action**: Long press the tile to jump directly to the settings activity.

### Tested Devices

* ✅ Xperia 1 II / 5 II / 1 V
* ⚠️ Other Xperia models may work if they support Dynamic Vibration in stock ROM.  
User feedback and testing reports are welcome.

### Credits

* **Implementation logic**: Based on works by [Alcatraz323](https://github.com/Alcatraz323).
* **Contributors**: hellobbn, wushidi.
* **Disclaimer**: Xperia and Dynamic Vibration are trademarks or technologies of **Sony Group Corporation**.  
  This project is not affiliated with or endorsed by Sony.
