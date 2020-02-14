package io.rinckd.pusher;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.util.Log;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import com.pusher.client.Pusher;
import java.util.HashMap;
import java.util.Map;
import io.rinckd.pusher.platform_messages.InstanceMessage;
import com.google.gson.Gson;
import com.pusher.client.channel.Channel;
import io.flutter.plugin.common.EventChannel;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
/** PusherPlugin */
public class PusherPlugin implements FlutterPlugin, MethodCallHandler {

  public static String TAG = "PusherPlugin";
  public static EventChannel.EventSink eventSink;
  private MethodChannel channel;
  private Map<String, PusherInstance> pusherInstanceMap = new HashMap<>();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "com.github.rinckd/pusher");
    channel.setMethodCallHandler(new PusherPlugin());
    final EventChannel eventStream = new EventChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "com.github.rinckd/pusherStream");


    eventStream.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object args, final EventChannel.EventSink eventSink) {
        PusherPlugin.eventSink = eventSink;
      }

      @Override
      public void onCancel(Object args) {
        Log.d(TAG, String.format("onCancel args: %s", args != null ? args.toString() : "null"));
      }
    });
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    channel = null;
    pusherInstanceMap = null;
  }


  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "com.github.rinckd/pusher");
    final EventChannel eventStream = new EventChannel(registrar.messenger(), "com.github.rinckd/pusherStream");

    channel.setMethodCallHandler(new PusherPlugin());


    eventStream.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object args, final EventChannel.EventSink eventSink) {
       PusherPlugin.eventSink = eventSink;
      }

      @Override
      public void onCancel(Object args) {
        Log.d(TAG, String.format("onCancel args: %s", args != null ? args.toString() : "null"));
      }
    });



  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    Log.d("PusherPlugin", String.format("Pusher Plugin (%s)", call.method));

    Type type = new TypeToken<InstanceMessage>(){}.getType();
    InstanceMessage instanceMessage = new Gson().fromJson(call.arguments.toString(), type);
    String instanceId = instanceMessage.getInstanceId();
    PusherInstance instance = getPusherInstance(instanceId);
    if (instance == null) {
      String message = String.format("Instance with id %s not found", instanceId);
      throw new IllegalArgumentException(message);
    }

    instance.onMethodCall(call, result);

  }

  private PusherInstance getPusherInstance(String instanceId) {
    if (instanceId != null && !pusherInstanceMap.containsKey(instanceId)) {
      pusherInstanceMap.put(instanceId, new PusherInstance(instanceId));
    }
    return pusherInstanceMap.get(instanceId);
  }


}


