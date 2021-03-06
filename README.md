<h1 align="center">
	<img src="https://i.imgur.com/rqOh0KW.png" alt="IA">
</h1>

# Coeus
Es una librería que permite entrenar redes neuronales utilizando aprendizaje automático, sin tutor o bases de datos. Utiliza el método de TDLambda learning el cual hace uso de las Trazas de Elegibilidad como método de asignación de crédito temporal, y puede ser ajustado configurando sus diferentes parámetros. Provee una implementación de Redes NTuplas y además una API para conectarse con librerías que proveen otras implementaciones de redes neuronales como por ejemplo Encog, Neuroph, etc.
Para mas detalles referirse al [Informe](https://docs.google.com/document/d/1arNnKmmV7xc9qDrgPNbtxQXO8b81HknmJQKCshfAzUU/edit?usp=sharing) del mismo.

## Instalación
El proyecto esta construido utilizando Gradle (incorporado en el 
repositorio). 

##### Requisitos
- Java JDK 8 o superior.
- Tener configurada la variable de entorno ***JAVA_HOME***. 

##### Instrucciones Recomendadas
- `gradlew clean`: limpia los directorios del proyecto.   
- `gradlew build`: compila el proyecto.
- `gradlew finalFatJar`: crea un jar con la librería lista para 
usar.  
- `gradlew junitPlatformTest`:  ejecuta los test de JUnit.
- `gradlew javadoc`:  compila javadoc.

## Licencia
[![GNU GPL v3.0](http://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl.html)
