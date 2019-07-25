# TFLite_MNIST_siamese
This is a demo for mnist siamese trainning on Android. Python file can be find on [keras](https://keras.io). 

## Step to generate TFLite file

* run *mnist_siamese.py* to generate *siamese.h5*

* run *convert_h5_tflite.py* to transform .h5 model to .tflite model.

## Bug lists

* Didn't find op for builtin opcode 'FULLY-CONNECTED' version '3'.

```
dependencies{
	implementation 'org.tensorflow:tensorflow-lite:0.0.0-nightly'
}
```
