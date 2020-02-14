import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:pusher/pusher.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  int _nativeRate;
  PusherClient _pusherClient;
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // String platformVersion;
    // // Platform messages may fail, so we use a try/catch PlatformException.
    // try {
    //   platformVersion = await Pusher.;
    // } on PlatformException {
    //   platformVersion = 'Failed to get platform version.';
    // }
    try {
      var options =
          PusherOptions(host: '10.0.2.2', port: 6001, encrypted: false);
      PusherClient pusher = PusherClient('app', options, enableLogging: true);

      pusher
          .subscribe('channel')
          .bind('event', (event) => debugPrint('event =>' + event.toString()));
    } on PlatformException {
      _platformVersion = 'Failed to get platform version.';
    }
    // int r = await Pusher.nativeRate;

    // // If the widget was removed from the tree while the asynchronous platform
    // // message was in flight, we want to discard the reply rather than calling
    // // setState to update our non-existent appearance.
    // if (!mounted) return;

    setState(() {
      _nativeRate = 45;
      _platformVersion = 'test';
    });
  }

  void newTest() {
    debugPrint('he');
    try {
      var url = 'https://mywebsite/pusher/auth';
      var bearer = 'bearerToken';
      String pusherKey = 'pusherKey';
      var auth = PusherAuth(url, headers: {'Authorization': 'Bearer $bearer'});
      var options = PusherOptions(encrypted: true, auth: auth);
      _pusherClient = PusherClient(pusherKey, options, enableLogging: true);

      _pusherClient.subscribe('channel').bind('pusher:subscription_succeeded',
          (event) => debugPrint('MYevent =>' + event.toString()));
    } on PlatformException {
      _platformVersion = 'Failed to get platform version.';
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            Center(
              child: Text('native rate is $_nativeRate'),
            ),
            RaisedButton(
              child: Text('start'),
              onPressed: () => newTest(),
            ),
            RaisedButton(
              child: Text('start'),
              onPressed: () => newTest(),
            ),
            Center(
              child: Text('Running on: $_platformVersion\n'),
            ),
          ],
        ),
      ),
    );
  }
}
