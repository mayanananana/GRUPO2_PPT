# 🎮 Piedra, Papel o Tijeras - Versión CONSOLA (Educativa)

## 📍 Estás en la rama: `base-sin-interfaz`

Esta es la **versión pura y educativa** del proyecto, donde puedes ver claramente cómo funciona la arquitectura **Cliente-Servidor** con **Sockets TCP**.

---

## 🚀 Cómo Ejecutar

### Paso 1: Terminal del Servidor
```bash
gradle runServer
```

Verás algo como:
```
Bienvenido a Piedra, Papel y Tijera arrancando en el puerto 9999
Conecta varios jugadores a la vez
```

El servidor está esperando conexiones...

### Paso 2: Terminal del Cliente (en otra ventana/terminal)
```bash
gradle runClient
```

Verás algo como:
```
--------- Iniciando Cliente de PPT ---------
SERVIDOR: BIENVENIDO A PIEDRA, PAPEL O TIJERAS
SERVIDOR: 
--- RONDA 1 ---
Ingresa tu jugada (PIEDRA, PAPEL, TIJERAS) o SALIR para terminar:
Tu jugada > 
```

Ahora **escribe tu jugada** (PIEDRA, PAPEL o TIJERAS)

---

## 🎮 Flujo de Juego

```
1. Cliente se conecta al servidor (localhost:9999)
2. Servidor envía bienvenida
3. Por cada ronda (máx 3):
   - Servidor pide tu jugada
   - Tú escribes: PIEDRA, PAPEL o TIJERAS
   - Servidor decide (aleatoriamente)
   - Servidor anuncia ganador de la ronda
   - Muestra marcador
4. Al terminar 3 rondas (primer llegado a 2 puntos gana):
   - ¿Otra partida? (SI/NO)
   - SI → Vuelve al paso 3
   - NO → Desconexión

```

---

## 📂 Archivos Principales

### `ServidorPPT.java`
```java
public class ServidorPPT {
    private static final int PUERTO = 9999;
    
    public static void main(String[] args) {
        ServerSocket serverSocket = new ServerSocket(PUERTO);
        // Acepta clientes en un bucle
        while (activo) {
            Socket cliente = serverSocket.accept();
            // Delega a HandlerPPT en un hilo nuevo
            new Thread(new HandlerPPT(cliente)).start();
        }
    }
}
```

**Lo que hace:**
- 🔗 Escucha en puerto 9999
- 👥 Acepta múltiples clientes
- 🧵 Crea un hilo para cada cliente

---

### `HandlerPPT.java`
```java
public class HandlerPPT implements Runnable {
    // Lógica del juego:
    // - Lectura de jugadas del cliente
    // - Generación aleatoria de jugada del servidor
    // - Cálculo de ganador (piedra > tijera > papel > piedra)
    // - Control de puntuación
    // - Gestión de desempate
}
```

**Lo que hace:**
- 🎮 Gestiona UNA partida completa
- 🎲 Genera jugadas aleatorias
- 🏆 Calcula ganadores
- 🔄 Permite múltiples partidas

---

### `ClientePPT.java`
```java
public class ClientePPT {
    public static void main(String[] args) {
        Socket socket = new Socket("localhost", 9999);
        
        // Lee mensajes del servidor
        while ((lineaServidor = in.readLine()) != null) {
            System.out.println("SERVIDOR: " + lineaServidor);
            
            // Si pide jugada, lee del teclado
            if (lineaServidor.contains("tu jugada")) {
                String jugada = teclado.nextLine();
                out.println(jugada);
            }
        }
    }
}
```

**Lo que hace:**
- 🔌 Se conecta al servidor
- 📨 Lee mensajes del servidor
- ⌨️ Lee entrada del usuario
- 📤 Envía jugadas

---

## 🔍 Qué Aprender Aquí

### 1️⃣ Sockets TCP
```java
// Servidor esperando
ServerSocket serverSocket = new ServerSocket(9999);
Socket cliente = serverSocket.accept();

// Cliente conectando
Socket socket = new Socket("localhost", 9999);
```

### 2️⃣ Streams de Red
```java
// Lectura
BufferedReader in = new BufferedReader(
    new InputStreamReader(socket.getInputStream())
);

// Escritura
PrintWriter out = new PrintWriter(
    socket.getOutputStream(), true
);
```

### 3️⃣ Multithreading
```java
// Cada cliente en su propio hilo
new Thread(new HandlerPPT(socket)).start();
```

### 4️⃣ Protocolo Personalizado
```
Cliente → "PIEDRA" → Servidor
Servidor → "Ganaste" → Cliente
Cliente → "SI" → Servidor (jugar otra)
```

---

## 🎯 Ejercicios Educativos

### ✏️ Ejercicio 1: Modificar Jugadas
Cambia las opciones de 3 a 4 (agrega "LAGARTIJA" para Piedra, Papel, Lagartija, Tijeras)

**Archivos a modificar:**
- `HandlerPPT.java`: línea `opciones`
- `ClientePPT.java`: línea validación

### ✏️ Ejercicio 2: Cambiar a 5 Rondas
Haz que gane quien llegue a **3 puntos** en lugar de 2

**Archivo a modificar:**
- `HandlerPPT.java`: línea `rondasJugadas < 3` → `rondasJugadas < 5`

### ✏️ Ejercicio 3: Agregar Log de Conexiones
Crea un archivo `conexiones.log` donde se registren IP, hora y resultado

**Archivo a crear:**
- `LoggerPPT.java` - Nueva clase para guardar logs

### ✏️ Ejercicio 4: Modo Espectador
Permite que un cliente se conecte solo para **ver** partidas de otros sin jugar

---

## 🐛 Debugging

### Ver qué está pasando en servidor:
El servidor muestra:
```
NUEVO JUGADOR: 127.0.0.1
Cliente desconectado: ...
```

### Ver qué está pasando en cliente:
El cliente muestra:
```
SERVIDOR: [mensaje recibido]
Tu jugada > [lo que escribiste]
```

### Si no se conecta:
1. ✅ ¿Está el servidor corriendo?
2. ✅ ¿Puerto 9999 disponible? (`netstat -an | grep 9999` en consola)
3. ✅ ¿Firewall bloqueando?

---

## 💡 Conceptos Clave

| Concepto | Dónde Vejo | Propósito |
|----------|-----------|----------|
| **ServerSocket** | `ServidorPPT.java` | Escuchar conexiones |
| **Socket** | Ambos archivos | Conexión TCP |
| **Runnable** | `HandlerPPT.java` | Código ejecutable en hilo |
| **BufferedReader** | Ambos archivos | Leer del socket |
| **PrintWriter** | Ambos archivos | Escribir al socket |
| **Random** | `HandlerPPT.java` | Generar jugadas |

---

## 🔗 Próximos Pasos

Cuando domines esta versión, ve a la rama **`main`** para ver cómo se integra esta misma lógica en una **interfaz gráfica con JavaFX**.

```bash
git checkout main
```

---

## 📝 Notas Importantes

- ⚠️ **Una instancia `HandlerPPT` por cliente** - Cada cliente tiene su propio hilo
- ⚠️ **El servidor es el "bot"** - El cliente juega contra el servidor
- ⚠️ **Primer a 2 puntos gana** - En 3 rondas máximo (puede haber desempate)
- ⚠️ **Ctrl+C para cerrar** - Tanto servidor como cliente

---

**¡Aprende, experimenta y diviértete!** 🎮
