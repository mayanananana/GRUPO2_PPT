# 🎮 Piedra, Papel o Tijeras - Arquitectura Cliente-Servidor

Un juego educativo de **Piedra, Papel o Tijeras** que demuestra la arquitectura **Cliente-Servidor** con **Sockets TCP** en Java.

## 📋 Estructura de Ramas

Este proyecto tiene **2 ramas principales** que ilustran la evolución del código:

### 🔧 `base-sin-interfaz`
**Versión pura de consola** - El primer paso educativo

- **Servidor en consola**: Ejecutable desde terminal, muestra todos los eventos
- **Cliente en consola**: Interactúa desde otra terminal, ingresa jugadas por teclado
- **Sin dependencias gráficas**: Solo Java nativo con Sockets
- **Perfecto para entender**: El flujo básico de Cliente-Servidor

**Cómo ejecutar:**

```bash
# Terminal 1: Inicia el servidor
gradle runServer

# Terminal 2: Inicia el cliente (mientras el servidor está corriendo)
gradle runClient

# El cliente puede cerrarse con Ctrl+C
# El servidor puede cerrarse con Ctrl+C
```

**Flujo en consola:**
```
[SERVIDOR]                        [CLIENTE]
Esperando...                      
  ✓ Cliente conectado             Conectando...
  ✓ Bienvenido                    ✓ Conectado
  [esperando jugada]              > PIEDRA
  ✓ Has jugado PIEDRA             Ganancias: X-Y
  ...
```

---

### 🎨 `main`
**Versión con Interfaz Gráfica JavaFX** - La evolución del código

- **Misma lógica de Sockets**: Reutiliza los componentes de comunicación
- **Interfaz gráfica**: JavaFX FXML para una experiencia visual
- **Servidor integrado**: Se lanza dentro de la GUI
- **Cliente integrado**: Comunica con el servidor gráficamente
- **Opción de consola**: También puedes ejecutar servidor/cliente en consola

**Cómo ejecutar:**

```bash
# Ejecutar la GUI (servidor y cliente integrados)
gradle runGUI
# o simplemente:
gradle run

# Si quieres ver los logs en consola mientras usas la GUI:
# Terminal 1: 
gradle runServer

# Luego ejecuta la GUI en el IDE o con:
gradle runGUI
```

---

## 🏗️ Arquitectura Técnica

### Componentes Principales

1. **`ServidorPPT.java`** (Servidor multihilo)
   - Escucha en puerto 9999
   - Acepta múltiples clientes simultáneamente
   - Delega cada conexión a un hilo `HandlerPPT`

2. **`HandlerPPT.java`** (Manejador de cliente)
   - Implementa `Runnable` para ejecutarse en hilo separado
   - Gestiona una partida completa con el cliente
   - Lógica del juego: Piedra > Tijeras > Papel > Piedra
   - Primer jugador que llegue a 2 puntos gana

3. **`ClientePPT.java`** (Cliente)
   - Se conecta al servidor en `localhost:9999`
   - Versión consola: Lee del teclado, imprime respuestas
   - Versión GUI: Se integra con la interfaz JavaFX

4. **`InterfazController.java`** & **`Launcher.java`** (GUI)
   - `Launcher`: Punto de entrada JavaFX
   - `InterfazController`: Controla la lógica de UI y conecta con servidor/cliente
   - Botones para Piedra, Papel, Tijera
   - Área de registro de eventos

---

## 📊 Flujo de Comunicación

```
Cliente                                Servidor
  |                                       |
  |-------- CONEXIÓN SOCKET ------------>|
  |                                       |
  |<----- "BIENVENIDO A PPT" ------------|
  |                                       |
  |<----- "¿Qué juegas?" ----------------| (Handler esperando)
  |                                       |
  |-------- "PIEDRA" ------------------>|
  |                                       | (Handler decide: PAPEL)
  |<----- "Ganaste esta ronda" ---------|
  |                                       |
  |<----- "¿Otra partida?" -------------|
  |                                       |
  |-------- "SI" --------------------->|
  |                                       |
  |<----- (Nueva partida comienza) -----|
  |                                       |
  |-------- "NO" --------------------->|
  |                                       |
  |<----- "Gracias por jugar" ---------|
  |                                       |
  |-------- CIERRA SOCKET ------------>|
```

---

## 🔄 Diferencias entre Ramas

| Aspecto | `base-sin-interfaz` | `main` |
|--------|-------------------|--------|
| **Interfaz** | Consola | JavaFX FXML |
| **Servidor Visible** | Terminal propia | Dentro de la GUI |
| **Cliente Visible** | Terminal propia | Dentro de la GUI |
| **Entrada de Usuario** | `Scanner` (teclado) | Botones gráficos |
| **Salida** | `System.out.println` | TextArea + Consola |
| **Caso de uso** | Educacional/Debugging | Presentación final |

---

## 🚀 Casos de Uso

### 📚 Para Aprender
1. Ir a `base-sin-interfaz`
2. Leer `ServidorPPT.java` y `HandlerPPT.java`
3. Ejecutar con `gradle runServer` y `gradle runClient`
4. Ver los logs en tiempo real

### 🎯 Para Presentar
1. Cambiar a `main`
2. Ejecutar `gradle runGUI`
3. Mostrar la interfaz gráfica en acción
4. Opcionalmente, abrir otra terminal con `gradle runServer` para ver logs del servidor

### 🔧 Para Depurar
1. En `base-sin-interfaz`: Los logs van a consola, muy claros
2. En `main`: Combinación de logs en consola + TextArea GUI
3. Ambas permiten ver exactamente qué ocurre en red

---

## 📝 Protocolo de Juego

El servidor espera estos comandos del cliente:

- `PIEDRA`, `PAPEL`, `TIJERAS` - Jugada válida
- `SI`, `NO` - Respuesta a "¿Otra partida?"
- `SALIR` - Abandonar la partida (solo en consola)

---

## ⚙️ Requisitos

- **Java 21+**
- **Gradle 9.2+**
- **JavaFX 21** (solo para rama `main`)

---

## 📌 Comandos Útiles

```bash
# Ver todas las ramas disponibles
git branch -a

# Cambiar a rama sin interfaz
git checkout base-sin-interfaz

# Cambiar a rama con interfaz
git checkout main

# Ver cambios entre ramas
git diff base-sin-interfaz main

# Ver historial de commits
git log --oneline
```

---

## 💡 Conceptos Educativos

Este proyecto demuestra:

✅ **Sockets TCP** - Comunicación bidireccional  
✅ **Multithreading** - Servidor maneja múltiples clientes  
✅ **Protocolo Cliente-Servidor** - Patrón de comunicación  
✅ **Streams de Red** - Lectura/escritura sobre sockets  
✅ **Integración con GUI** - JavaFX + lógica de red  
✅ **Control de flujo** - Ciclos de juego, ganadores, desempates  

---

## 👥 Equipo

Grupo 2 - PSP (Programación de Servicios y Procesos)

---

**¡Diviértete jugando y aprendiendo sobre arquitectura de red!** 🎮
