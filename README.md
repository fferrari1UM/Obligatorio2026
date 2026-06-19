# Obligatorio 2026 — Programación II

Implementación del módulo de administración de procesos para el sistema operativo **Doors**, desarrollado para la empresa ficticia MacrosUM.

## Descripción

El sistema gestiona procesos de usuarios con sus respectivos eventos (CPU, RAM, DISK), administra su ciclo de vida completo (NEW → PENDING → RUNNING → FINISHED) y mantiene un log de todos los eventos del sistema.

## Estructura del proyecto

```
Obligatorio2026/
├── obligatorio2026/
│   ├── src/main/java/uy/edu/um/
│   │   ├── Main.java
│   │   ├── doors/
│   │   │   ├── ProcessManager.java       # Interfaz principal
│   │   │   ├── ProcessManagerImpl.java   # Implementación
│   │   │   ├── ProcessConsole.java       # CLI interactiva
│   │   │   ├── Proceso.java
│   │   │   ├── Usuario.java
│   │   │   └── Evento.java
│   │   └── tad/
│   │       ├── hash/                     # HashMap propio
│   │       ├── heap/                     # MaxHeap propio
│   │       ├── list/                     # LinkedList propia
│   │       ├── queue/                    # Queue propia
│   │       ├── stack/                    # Stack propia
│   │       └── binarytree/               # BST propio
│   ├── process.csv                       # Archivo de prueba de procesos
│   └── users.csv                         # Archivo de prueba de usuarios
└── README.md
```

## Cómo ejecutar

Cloná el repositorio, abrí el proyecto en IntelliJ IDEA e importá `obligatorio2026/pom.xml` como proyecto Maven. Luego ejecutá `Main.java` --> aparece el prompt `doors>` en la consola.

### 3. Cargar datos de prueba

Los archivos CSV de prueba (`process.csv` y `users.csv`) están incluidos en la carpeta `obligatorio2026/`. Una vez que el sistema esté corriendo, escribí el siguiente comando en la consola:

```
pload -p obligatorio2026/process.csv -u obligatorio2026/users.csv
```

> **Nota:** Si el sistema no encuentra los archivos, usá la ruta absoluta:
> ```
> pload -p /ruta/a/tu/proyecto/Obligatorio2026/obligatorio2026/process.csv -u /ruta/a/tu/proyecto/Obligatorio2026/obligatorio2026/users.csv
> ```

---

## Comandos disponibles

### `pload` — Cargar datos
```
pload -p <path_csv_procesos> -u <path_csv_usuarios>
```
Carga procesos y usuarios desde archivos CSV. Los procesos quedan en estado NEW.

### `pprepare` — Preparar procesos
```
pprepare
```
Toma los procesos en estado NEW, calcula su prioridad y los mueve a la cola de pendientes (PENDING).

**Fórmula de prioridad:**
```
P(p) = (8·N_CPU + 2·N_RAM + 2·N_DISK) / N_events + W · N_events
```
- `W = 32` si el usuario es ADMIN
- `W = 16` si el usuario es GENERIC

### `pexecute` — Ejecutar proceso
```
pexecute
```
Ejecuta el proceso pendiente con mayor prioridad. Solo puede haber un proceso en ejecución a la vez.

### `pfinish` — Finalizar proceso
```
pfinish OK              # Finalización exitosa
pfinish ERROR           # Finalización con error
pfinish TERM <UID>      # Terminación forzada por el usuario con ese UID
```

### `pstatus` — Consultar estado
```
pstatus                 # Estado general (ejecución, pendientes, finalizados)
pstatus -verbose        # Incluye detalle completo de eventos de cada proceso
pstatus -u <UID>        # Muestra solo los procesos del usuario indicado
pstatus -p <PID>        # Muestra el detalle completo de un proceso específico
```

### Otros
```
help    # Muestra la lista de comandos disponibles
exit    # Sale del sistema
```

---

## Sesión de ejemplo

```
doors> pload -p obligatorio2026/process.csv -u obligatorio2026/users.csv
doors> pprepare
doors> pexecute
doors> pstatus
doors> pfinish OK
doors> pstatus
doors> exit
```

---

## Log del sistema

Cada evento se registra automáticamente en un archivo generado en la raíz del proyecto con el formato:
```
DOORS_PROCESS_LOG_yyyy-MM-dd
```

Ejemplo de entradas:
```
[2026-03-01 15:23:36]: NEW PENDING PROCESS: PID=123 | notepad.exe | USER:admin UID:525 | P=8541
[2026-03-01 15:33:36]: EXECUTING PROCESS: PID=123 | USER:admin UID:525
 EVENT: CPU | Instructions [sum, jump, add]
 EVENT: RAM | Instructions [read, write]
[2026-03-01 16:33:36]: ENDING PROCESS: PID=123 | STATE: OK
```

Cuando la pila de procesos finalizados supera la capacidad máxima (`MAX_FINISHED_PROCESS_ON_RAM = 3`), se vuelca su contenido al log antes de descartar los procesos más antiguos.

---

## TADs utilizados

| Estructura | Uso |
|---|---|
| `MyQueueImpl` | Cola de procesos NEW |
| `MyHeapImpl` (max-heap) | Cola de prioridad de procesos PENDING |
| `MyStackImpl` | Pila de procesos FINISHED |
| `MyHashImpl` | Mapa de usuarios por UID |
| `MyLinkedListImpl` | Lista de eventos de cada proceso |
