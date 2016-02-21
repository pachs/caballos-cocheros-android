# caballos-cocheros-android

Haar training
--------------
- [Informacion genereal del archivo de entranamiento para haar cascade](http://note.sonots.com/SciSoftware/haartraining.html#v6f077ba)

#### Programa para crear el xml a partir de imagenes positivas y negativas.

- [Windows](http://nayakamitarup.blogspot.com.co/2011/07/how-to-make-your-own-haar-trained-xml.html)
- [Linux](http://opencvuser.blogspot.com.co/2011/08/creating-haar-cascade-classifier-aka.html)

Las imagenes positivas son las que tienen dentro el objeto que se quiere reconocer, las negativas son imagenes que no tienen el objeto que se quiere reconocer.

Para entrenar correctamente el modelo es necesario tener una gran cantidad de imagenes (entre mas imagenes haya, mejor es la certeza del reconocimiento). Se recomienda usar mas o menos 4500 imagenes, 3000 positivas y 1500 negativas (una proporcion de 2:1).

Haar cascade
-------------
- [Ejemplo de OpenCV para detectar objetos desde un stream de video usando Haar Cascade](http://docs.opencv.org/2.4/doc/tutorials/objdetect/cascade_classifier/cascade_classifier.html#cascade-classifier)
