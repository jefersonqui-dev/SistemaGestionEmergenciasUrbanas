# 🚨 Sistema de Gestión de Emergencias Urbanas (SGEU)

## 📝 Descripción
El Sistema de Gestión de Emergencias Urbanas (SGEU) es una aplicación Java diseñada para gestionar y coordinar la respuesta a emergencias urbanas de manera eficiente. El sistema permite registrar emergencias, asignar recursos, y monitorear el estado de las situaciones de emergencia en tiempo real.
image.png

## 🎯 Objetivos
-  Gestionar eficientemente las emergencias urbanas
-  Optimizar la asignación de recursos
-  Proporcionar una interfaz intuitiva para los operadores
-  Mantener un registro actualizado del estado de las emergencias

## ⚙️ Funcionalidades Principales

### 📋 1. Registro de Emergencias
-  Registro de nuevos incidentes con tipo, ubicación y nivel de gravedad
-  Clasificación automática de emergencias
-  Cálculo de tiempo de respuesta estimado

### 🚗 2. Gestión de Recursos
-  Visualización del estado de los recursos disponibles
-  Asignación inteligente de recursos a emergencias según su tipo o manualmente
-  Sugerencias de recursos basadas en el tipo de emergencia

### 📡 3. Monitoreo en Tiempo Real
-  Actualización automática del estado de las emergencias
-  Visualización de emergencias activas
-  Seguimiento del progreso de atención
-  Monitoreo de combustible

### 📊 4. Estadísticas y Reportes
-  Generación de estadísticas del sistema
-  Análisis de tiempos de respuesta
-  Evaluación de la eficiencia del sistema

## 🎓 Conceptos de POO Implementados

### 🔒 1. Encapsulamiento
-  Clases con atributos privados y métodos públicos
-  Control de acceso a los datos del sistema
-  Implementación de getters y setters

### 🔄 2. Herencia
-  Jerarquía de tipos de emergencias
-  Clases base para recursos y emergencias
-  Extensibilidad del sistema

### 🎭 3. Polimorfismo
-  Múltiples implementaciones de estrategias de cálculo de tiempo
-  Interfaz común para diferentes tipos de recursos
-  Comportamiento dinámico según el tipo de emergencia

### 🎨 4. Abstracción
-  Interfaces claras para la interacción con el sistema
-  Modelado de conceptos del mundo real
-  Separación de responsabilidades

## 🏗️ Patrones de Diseño

### 🎯 1. Singleton
-  Implementado en `EmergencySistem` para garantizar una única instancia del sistema
-  Control centralizado de la gestión de emergencias

### ⚡ 2. Strategy
-  Patrón utilizado para el cálculo de tiempos de respuesta
-  Permite cambiar dinámicamente la estrategia de cálculo

### 👀 3. Observer
-  Implementado para la actualización en tiempo real
-  Notificación automática de cambios en el estado

## 🚀 Cómo Usar la Aplicación

### 📋 Requisitos
-  IDE compatible con Java (recomendado)

### ⚙️ Instalación
1.  Clonar el repositorio
2.  Compilar el proyecto
3.  Ejecutar la clase `MainApp`

### 📱 Uso
1. 🚀 Iniciar la aplicación
2. 📋 Seleccionar una opción del menú principal:
   -  Registrar nueva emergencia
   -  Ver emergencias activas
   -  Ver estado de recursos
   -  Gestionar emergencias
   -  Ver estadísticas
   -  Salir

### 🔄 Flujo de Trabajo
1.  Registrar una emergencia con sus detalles
2.  Atención de emergencia y asignación de Recursos
3.  Monitorear el progreso de la emergencia
4.  Mostrar estadísticas

## 🤝 Contribución
Las contribuciones son bienvenidas. Por favor, asegúrese de:
1.  Hacer fork del proyecto
2.  Crear una rama para su feature
3.  Commit sus cambios
4.  Push a la rama
5.  Abrir un Pull Request


