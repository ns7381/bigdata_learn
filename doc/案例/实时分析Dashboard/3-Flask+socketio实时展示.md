##### Flask代码如下：

```python
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
```

##### SocketIo与hightcharts.js展示图表

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>DashBoard</title>
    <script src="static/js/socket.io.js"></script>
    <script src="static/js/jquery-3.1.1.min.js"></script>
    <script src="static/js/highcharts.js"></script>
    <script src="static/js/exporting.js"></script>
    <script type="text/javascript" charset="utf-8">
    var socket = io.connect('http://' + document.domain + ':' + location.port);
    socket.on('connect', function() {
        socket.emit('test_connect', {data: 'I\'m connected!'});
    });

    socket.on('test_message',function(message){
        console.log(message);
        var obj = JSON.parse(message);
        if ("girl" in obj) {
            $('#girl').html(obj["girl"]);
        } else if ("boy" in obj) {
            $('#boy').html(obj["boy"]);
        }
    });

    socket.on('connected',function(){
        console.log('connected');
    });

    socket.on('disconnect', function () {
        console.log('disconnect');
    });
    </script>
</head>
<body>
<div>
    <b>Girl: </b><b id="girl"></b>
    <b>Boy: </b><b id="boy"></b>
</div>
<div id="container" style="width: 600px;height:400px;"></div>

<script type="text/javascript">
    $(document).ready(function () {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    Highcharts.chart('container', {
        chart: {
            type: 'spline',
            animation: Highcharts.svg, // don't animate in old IE
            marginRight: 10,
            events: {
                load: function () {

                    // set up the updating of the chart each second
                    var series1 = this.series[0];
                    var series2 = this.series[1];
                    setInterval(function () {
                        var x = (new Date()).getTime(), // current time
                        count1 = $('#girl').text();
                        y = parseInt(count1);
                        series1.addPoint([x, y], true, true);

                        count2 = $('#boy').text();
                        z = parseInt(count2);
                        series2.addPoint([x, z], true, true);
                    }, 1000);
                }
            }
        },
        title: {
            text: '男女生购物人数实时分析'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 50
        },
        yAxis: {
            title: {
                text: '数量'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: true
        },
        exporting: {
            enabled: true
        },
        series: [{
            name: '女生购物人数',
            data: (function () {
                // generate an array of random data
                var data = [],
                    time = (new Date()).getTime(),
                    i;

                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: Math.random()
                    });
                }
                return data;
            }())
        },
        {
            name: '男生购物人数',
            data: (function () {
                // generate an array of random data
                var data = [],
                    time = (new Date()).getTime(),
                    i;

                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: Math.random()
                    });
                }
                return data;
            }())
        }]
    });
});
</script>
</body>
</html>
```

