# 🎮 Piedra, Papel o Tijeras - Versión GRÁFICA (Interfaz JavaFX)

## 📍 Estás en la rama: `main`

Esta es la **versión con interfaz gráfica** del proyecto, una **evolución** de la versión en consola que integra la misma lógica de sockets pero con una experiencia visual mejorada.

---

## 🚀 Cómo Ejecutar

### Opción 1: Con Interfaz Gráfica (TODO integrado)
```bash
gradle runGUI
# o simplemente:
gradle run
```

Se abrirá una ventana con:
- 🎮 Botones para Piedra, Papel, Tijera
- 📊 Visualización de jugadas
- 📈 Marcador en tiempo real
- 📝 Área de logs con los eventos del juego

---

### Opción 2: Modo Híbrido (Servidor en consola + GUI)

**Terminal 1 - Servidor en consola:**
```bash
gradle runServer
```

**Terminal 2 - Cliente con GUI:**
```bash
gradle runGUI
```

Así puedes ver los logs del servidor en una terminal mientras juegas en la GUI.

---

### Opción 3: Modo Consola Puro (sin GUI)

Si quieres ver la versión sin interfaz:

**Terminal 1:**
```bash
gradle runServer
```

**Terminal 2:**
```bash
gradle runClient
```

---

## 🎨 Interfaz Gráfica

### Elementos Principales

```
┌──────────────────────────────────────────────┐
│     Piedra, Papel o Tijeras - CLIENTE        │
├──────────────────────────────────────────────┤
│                                              │
│  [INICIAR PARTIDA]                          │
│                                              │
│  Tu Jugada:  🤔        Servidor: 🤔         │
│                                              │
│  [ 🪨 PIEDRA ] [ 📄 PAPEL ] [ ✂ TIJERA ]   │
│                                              │
│  Resultado: Esperando...                     │
│                                              │
│  Marcador: 0 - 0                             │
│                                              │
│  ┌────────────────────────────────────────┐  │
│  │ Logs:                                  │  │
│  │ [Log de eventos del juego]             │  │
│  │                                        │  │
│  └────────────────────────────────────────┘  │
│                                              │
│  [ SÍ ]  [ NO ]  (aparecen al final)         │
│                                              │
└──────────────────────────────────────────────┘
```

### Flujo de la GUI

1. **Al iniciar**: Solo el botón "INICIAR PARTIDA" está activo
2. **Al hacer click**: 
   - Servidor se crea en hilo daemon
   - Cliente se conecta automáticamente
   - Se habilitan los botones de jugada
3. **Al jugar**:
   - Haces click en Piedra/Papel/Tijera
   - Se muestra tu jugada
   - Se actualiza el marcador
4. **Al terminar ronda**: Se muestra el resultado
5. **Al terminar partida**: Aparecen botones Sí/No para otra partida

---

## 🏗️ Arquitectura Código (Main)

### Relación entre Clases

```
Launcher (main)
    ↓
InterfazUsuario (Application JavaFX)
    ↓
InterfazController (controla UI)
    ├→ ServidorPPT (corre en hilo daemon)
    │   └→ HandlerPPT (por cada cliente)
    │       └→ ClientePPT (para cada conexión)
    │
    └→ ClientePPT (cliente conectado)
```

### `Launcher.java`
Punto de entrada simple:
```java
public class Launcher {
    public static void main(String[] args) {
        InterfazUsuario.main(args);  // Inicia la GUI
    }
}
```

---

### `InterfazUsuario.java`
Extiende `Application` (JavaFX):
```java
public class InterfazUsuario extends Application {
    @Override
    public void start(Stage stage) {
        // Carga el archivo FXML
        // Configura la escena
        // Muestra la ventana
    }
}
```

---

### `InterfazController.java`
Controla toda la lógica de UI:

```java
public class InterfazController {
    
    @FXML private Button botonIniciar;
    @FXML private Button botonPiedra, botonPapel, botonTijera;
    @FXML private Label labelMarcador, labelResultadoRonda;
    @FXML private TextArea areaDeJuego;
    
    private ServidorPPT servidor;
    private ClientePPT cliente;
    
    @FXML
    private void iniciarJuego() {
        // 1. Crea servidor en hilo daemon
        servidor = new ServidorPPT(9999);
        new Thread(servidor).start();
        
        // 2. Conecta cliente
        cliente = new ClientePPT("localhost", 9999, this::mostrarMensajeEnLog);
        cliente.conectar();
    }
    
    @FXML
    private void jugarPiedra() {
        cliente.enviarMensaje("PIEDRA");
    }
    
    // Callback: cuando el servidor envía un mensaje
    private void mostrarMensajeEnLog(String mensaje) {
        Platform.runLater(() -> {
            areaDeJuego.appendText(mensaje + "\n");
        });
    }
}
```

---

### `ClientePPT.java` (versión GUI)
Mismo cliente, pero con callback:

```java
public class ClientePPT {
    private Consumer<String> onMessageReceived; // Callback a la GUI
    
    public ClientePPT(String host, int puerto, 
                      Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }
    
    public void conectar() {
        socket = new Socket(host, puerto);
        // Inicia hilo de escucha
        Thread listener = new Thread(this::escucharAlServidor);
        listener.setDaemon(true);
        listener.start();
    }
    
    private void escucharAlServidor() {
        String mensaje;
        while ((mensaje = in.readLine()) != null) {
            // Llama al callback para actualizar GUI
            onMessageReceived.accept(mensaje);
        }
    }
}
```

---

### `InterfazUsuario.fxml`
El archivo de layout FXML define la UI visualmente

---

### `ServidorPPT.java` y `HandlerPPT.java`
**Sin cambios** - Reutilizan la lógica de la versión consola:
- Mismo protocolo
- Mismo cálculo de ganador
- Mismo manejo de múltiples clientes

---

## 🔄 Comparación: Consola vs GUI

| Aspecto | Consola (`base-sin-interfaz`) | GUI (`main`) |
|--------|------|-----|
| **Entrada** | `Scanner` del teclado | Click en botones |
| **Salida** | `System.out.println` | TextArea |
| **Inicio** | `gradle runServer` + `gradle runClient` | `gradle runGUI` |
| **Servidor** | Terminal separada | Hilo daemon en la app |
| **Cliente** | Terminal separada | Integrado en la UI |
| **Logs** | Consola clara | TextArea + Consola |

---

## 🎯 Lo Mismo, Diferente Presentación

El **corazón del programa es idéntico**:

```
┌─────────────────────┐
│  ServidorPPT        │ ← Exactamente igual en ambas ramas
├─────────────────────┤
│  HandlerPPT         │ ← Exactamente igual
├─────────────────────┤
│  ClientePPT         │ ← Lógica idéntica, solo callback para GUI
└─────────────────────┘

Solo CAMBIA:
- ¿Cómo se INICIA?        Consola vs JavaFX
- ¿Cómo se INTERACTÚA?    Teclado vs Botones
- ¿Cómo se VEN LOGS?      Console vs TextArea
```

---

## 🧵 Multithreading en JavaFX

**Importante:** JavaFX no permite actualizar UI desde threads de red.

```java
// ❌ INCORRECTO: Desde hilo de red
onMessageReceived.accept(mensaje); // Crash!

// ✅ CORRECTO: Vuelve al hilo JavaFX
Platform.runLater(() -> {
    areaDeJuego.appendText(mensaje);
});
```

---

## 💻 Estructura de Threads

```
Main Thread (JavaFX UI)
    ├─ Hilo Servidor (daemon)
    │   ├─ Hilo Handler Cliente 1
    │   └─ Hilo Handler Cliente 2...
    └─ Hilo Cliente Listener (daemon)
        └ Escucha del servidor
```

---

## 🔧 Debugging en Main

### Ver logs del servidor:
Abre terminal y ejecuta:
```bash
gradle runServer
```

Verás los eventos del servidor mientras usas la GUI.

### Ver logs del cliente:
Ya están en la TextArea de la GUI.

### Ver ambos:
- Terminal 1: `gradle runServer`
- GUI: `gradle runGUI` desde el IDE

---

## 📚 Aprendizaje Progresivo

### Paso 1: Entender Consola
1. Ir a rama `base-sin-interfaz`
2. Leer `ServidorPPT.java` → Entender Sockets
3. Leer `HandlerPPT.java` → Entender Protocolo
4. Ejecutar `gradle runServer` y `gradle runClient`

### Paso 2: Entender GUI
1. Volver a rama `main`
2. Leer `InterfazController.java` → Ver cómo se integra
3. Notar que `ServidorPPT.java` es **idéntico**
4. Ver cómo `ClientePPT.java` usa **callbacks**

### Paso 3: Extensiones
- Agregar chat entre jugadores
- Mostrar histórico de partidas
- Guardar stats en base de datos
- Agregar más jugadores simultáneos

---

## 🎮 Ejercicios

### ✏️ Ejercicio 1: Cambiar Colores
Modifica los estilos CSS del FXML para cambiar colores

### ✏️ Ejercicio 2: Agregar Sonidos
Agrega sonidos al ganar/perder (JavaFX Media)

### ✏️ Ejercicio 3: Mostrar IP del Cliente
En el área de logs, muestra la IP del cliente conectado

### ✏️ Ejercicio 4: Desactivar Botones Correctamente
Ahora mismo se pueden hacer click mientras el servidor está pensando. Arréglalo.

### ✏️ Ejercicio 5: Animaciones
Agrega animaciones cuando ganas/pierdes una ronda

---

## ⚠️ Cosas Importantes

1. **El servidor y cliente corren en la misma app**: Esto es para demostración. En producción estarían en máquinas diferentes.

2. **Solo un cliente**: Actualmente solo puedes jugar desde una GUI. Si ejecutas dos `gradle runGUI`, ambas se conectarán y compartirán servidor.

3. **Hilo Daemon**: El servidor es daemon, así que cerrando la GUI se cierran todos los threads.

4. **Platform.runLater()**: SIEMPRE úsalo para actualizar UI desde threads de red.

---

## 🔗 Relacionado

- 📄 **README.md** - Visión general del proyecto
- 📄 **CONSOLA_README.md** - Guía para rama `base-sin-interfaz`
- 🌳 **base-sin-interfaz** - Versión pura sin interfaz

---

## 🎯 Conceptos Demostrados Aquí

✅ **Integración UI + Red** - GUI actualiza con eventos de red  
✅ **Callbacks** - UI se entera de eventos del cliente  
✅ **Thread Safety** - Platform.runLater para UI segura  
✅ **Reutilización de Código** - ServidorPPT idéntico en ambas ramas  
✅ **Evolución de Software** - De consola a GUI manteniendo lógica  

---

**¡Juega, aprende y experimenta!** 🎮
