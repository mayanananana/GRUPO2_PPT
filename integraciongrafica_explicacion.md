# Integración Gráfica de Piedra, Papel o Tijera

Se ha completado la integración de la lógica de juego de Piedra, Papel o Tijera (PPT) con la interfaz gráfica de usuario (GUI). A continuación, se detalla el proceso y el resultado:

## Cambios Realizados

1.  **`ServidorPPT.java`**:
    *   Se refactorizó para ser una clase instanciable (`ServidorPPT` ahora implementa `Runnable`).
    *   Se eliminó el método `main` y la lógica de `ShutdownHook`.
    *   Ahora se inicia y detiene a través de métodos públicos (`run()` y `detener()`), lo que permite que sea gestionado desde el controlador de la interfaz en un hilo separado.

2.  **`ClientePPT.java`**:
    *   Se rediseñó completamente para operar de forma asíncrona, eliminando el método `main` y la interacción por consola (`Scanner`).
    *   El constructor ahora acepta un `Consumer<String>`, que es una función callback que el controlador de la interfaz provee. Esta función se ejecuta cada vez que el cliente recibe un mensaje del servidor, permitiendo actualizar la UI de forma segura.
    *   Se añadió un hilo de escucha (`listenerThread`) que se encarga de leer los mensajes del servidor sin bloquear el hilo principal de la aplicación.
    *   Se implementaron métodos `conectar()`, `enviarMensaje(String)` y `desconectar()` para una gestión controlada desde la GUI.

3.  **`InterfazUsuario.fxml`**:
    *   Se añadió un `Button` con `fx:id="botonIniciar"` y `onAction="#iniciarJuego"` para que el usuario pueda iniciar la partida.
    *   Se incluyó un `TextArea` con `fx:id="areaDeJuego"` (`editable="false"`, `wrapText="true"`) para mostrar el log de la comunicación entre el cliente y el servidor, reemplazando la `Label` `resultado` para un registro más completo.
    *   El `Label` `resultado` se mantiene, pero ahora es `visible="false"`, ya que el `TextArea` gestiona la mayor parte de la salida.

4.  **`InterfazController.java`**:
    *   Es el nuevo "cerebro" de la aplicación, orquestando la interacción entre la GUI y la lógica del juego.
    *   Se agregaron las variables `@FXML` correspondientes a los nuevos componentes (`botonIniciar`, `areaDeJuego`).
    *   Se incluyeron instancias de `ServidorPPT` y `ClientePPT`.
    *   El método `initialize()` ahora deshabilita los botones de jugada al inicio, dejando solo activo el botón "Iniciar Partida".
    *   **Método `iniciarJuego()`**:
        *   Crea y arranca el `ServidorPPT` en un **hilo separado** (`new Thread(servidor).start()`) para evitar el bloqueo de la interfaz gráfica.
        *   Instancia `ClientePPT`, pasándole una función lambda (`this::mostrarMensajeEnLog`) como callback para que el cliente pueda actualizar el `areaDeJuego`.
        *   La función `mostrarMensajeEnLog` utiliza `Platform.runLater()` para garantizar que las actualizaciones de la UI se realicen en el hilo de la aplicación JavaFX, previniendo errores.
        *   Conecta el cliente al servidor.
        *   Deshabilita `botonIniciar` y habilita los botones de jugada (Piedra, Papel, Tijera).
    *   Los métodos `elegirPiedra()`, `elegirPapel()`, `elegirTijera()` ahora envían la jugada correspondiente al servidor a través del `ClientePPT` y registran la acción en el `areaDeJuego`.
    *   Se añadió un método `detener()` para un cierre limpio de los servicios del cliente y el servidor, el cual debería ser invocado al cerrar la aplicación principal.

## ¿Cómo usar la aplicación ahora?

1.  **Ejecutar la Aplicación**: Inicia la aplicación JavaFX como lo harías normalmente.
2.  **Iniciar la Partida**: En la interfaz gráfica, pulsa el botón **"Iniciar Partida"**.
    *   Verás mensajes en el `TextArea` indicando que el servidor se ha iniciado y el cliente se ha conectado.
    *   El servidor te dará la bienvenida y te pedirá tu primera jugada.
3.  **Realizar una Jugada**: Haz clic en los botones **"🪨 Piedra"**, **"📄 Papel"** o **"✂ Tijera"**.
    *   Tu elección se enviará al servidor.
    *   El `TextArea` mostrará tu jugada y, seguidamente, la respuesta del servidor (la jugada del bot, el resultado de la ronda, el marcador, etc.).
4.  **Continuar Jugando**: Sigue las instrucciones que aparezcan en el `TextArea` para jugar más rondas o decidir si quieres jugar otra partida. Toda la comunicación entre tú y el servidor se gestionará a través de la interfaz.

### Próximo Paso Sugerido

Para asegurar un cierre completamente limpio de la aplicación, se recomienda modificar el archivo principal de la aplicación (`InterfazUsuario.java`) para que, al cerrar la ventana, se invoque el método `detener()` del `InterfazController`. Esto garantizará que los hilos del servidor y el cliente se cierren correctamente.
