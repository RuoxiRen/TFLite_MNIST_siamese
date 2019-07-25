from tensorflow.keras.backend import clear_session
import numpy as np
import tensorflow as tf
from tensorflow.keras import backend as K
clear_session()
np.set_printoptions(suppress=True)
input_graph_name = "siamese.h5"
output_graph_name = "siamese.tflite"

def contrastive_loss(y_true, y_pred):
    margin = 1
    square_pred = K.square(y_pred)
    margin_square = K.square(K.maximum(margin - y_pred, 0))
    return K.mean(y_true * square_pred + (1 - y_true) * margin_square)


converter = tf.lite.TFLiteConverter.from_keras_model_file(model_file=input_graph_name,custom_objects={'contrastive_loss':contrastive_loss})


converter.post_training_quantize = True
#在windows平台这个函数有问题，无法正常使用
tflite_model = converter.convert()
open(output_graph_name, "wb").write(tflite_model)
print ("generate:",output_graph_name)
