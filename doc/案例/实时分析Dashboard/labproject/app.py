import json
from flask import Flask, render_template
from flask_socketio import SocketIO
from kafka import KafkaConsumer


app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)
thread = None
consumer = KafkaConsumer('result', bootstrap_servers='hdinsight-20190409175205-62-master-3.novalocal:6667')

def background_thread():
    girl = 0
    boy = 0
    for msg in consumer:
        data_json = msg.value.decode('utf8')
        data_dict = json.loads(data_json)
        if data_dict['value'] == '0' :
            girl = data_dict['count']
            result = {"girl": girl}
        elif data_dict['value'] == '1':
            boy = data_dict['count']
            result = {"boy": boy}
        print(result)
        socketio.emit('test_message',json.dumps(result))


@socketio.on('test_connect')
def connect(message):
    print(message)
    global thread
    if thread is None:
        thread = socketio.start_background_task(target=background_thread)
    socketio.emit('connected', {'data': 'Connected'})


@app.route("/")
def handle_mes():
    return render_template("index.html")


if __name__ == '__main__':
    socketio.run(app,debug=False)