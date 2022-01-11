import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_slidable/flutter_slidable.dart';


void main() {
  runApp(MyApp());
}

class NotificationItem extends StatelessWidget {
  NotificationItem({Key key, this.notification, this.parent}) : super(key: key);

  final List notification;
  final _MyHomePageState parent;

  @override
  Widget build(BuildContext context) {
    final String appName = notification[0];
    final String notificationTitle = notification[1];
    final String notificationDescription = notification[2];
    final String notificationID = notification[3];

    return Slidable(
      actionPane: SlidableDrawerActionPane(),
      actionExtentRatio: 0.25,
      child: Container(
        color: Colors.white,
        child: ListTile(
          leading: CircleAvatar(
            backgroundColor: Colors.indigoAccent,
            child: Text(appName),
            foregroundColor: Colors.white,
          ),
          title: Text(notificationTitle),
          subtitle: Text(notificationDescription),
        ),
      ),
      actions: <Widget>[
        IconSlideAction(
          caption: 'Archive',
          color: Colors.blue,
          icon: Icons.archive,
          onTap: () => print('Archive'),
        ),
        IconSlideAction(
          caption: 'Share',
          color: Colors.indigo,
          icon: Icons.share,
          onTap: () => print('Share'),
        ),
      ],
      secondaryActions: <Widget>[
        IconSlideAction(
          caption: 'More',
          color: Colors.black45,
          icon: Icons.more_horiz,
          onTap: () => print('More'),
        ),
        IconSlideAction(
          caption: 'Delete',
          color: Colors.red,
          icon: Icons.delete,
          onTap: () => parent._deleteNotification(notificationID),
        ),
      ],
    );
  }
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('flutter_app_notifs.example.com/notifications');

  List _notifications = [['Unset App', 'Unset Title', 'Unset Description', 'Unset ID']];
  int _counter = 0;

  _MyHomePageState() {
    _onNotification();
  }

  Future<void> _onNotification() async {
    print('Called _onNotification');
    try {
      final int result = await platform.invokeMethod('loadNewNotificationHandler');
      print('Notification Got in _onNotification.');
      _getNotifications();
    } on PlatformException catch (e) {
      print("Failed in _onNotification: '${e.message}'.");
    }

    _onNotification();
  }

  Future<void> _getNotifications() async {
    List notifications;
    try {
      final List result = await platform.invokeMethod('getNotifications');
      notifications = result;
    } on PlatformException catch (e) {
      notifications = ["Failed to get notifications: '${e.message}'."];
    }

    setState(() {
      _notifications = notifications;
    });
  }

  Future<void> _sendNotification(String str) async {
    try {
      await platform.invokeMethod('sendNotification', <String, dynamic>{
        'str': str
      });
    } on PlatformException catch (e) {
    }
  }

  Future<void> _deleteNotification(String str) async {
    try {
      await platform.invokeMethod('deleteNotification', <String, dynamic>{
        'str': str
      });
      await _getNotifications();
    } on PlatformException catch (e) {
    }
  }

  void _incrementCounter() {
    _sendNotification(_counter.toString());
    setState(() {
      // This call to setState tells the Flutter framework that something has
      // changed in this State, which causes it to rerun the build method below
      // so that the display can reflect the updated values. If we changed
      // _counter without calling setState(), then the build method would not be
      // called again, and so nothing would appear to happen.
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      /*
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Invoke "debug painting" (press "p" in the console, choose the
          // "Toggle Debug Paint" action from the Flutter Inspector in Android
          // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
          // to see the wireframe for each widget.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
       */
      body: ListView(
        padding: const EdgeInsets.all(0),
        children: <Widget>[
          Container(
            height: 50,
            color: Colors.amber[100],
            child: Center(
              child: ElevatedButton(
                child: Text('Get Notifications'),
                onPressed: _getNotifications,
              ),
            )
          ),
          for ( var notification in _notifications ) NotificationItem(
              notification: notification,
              parent: this,
          ),
        ],
      ),

      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
