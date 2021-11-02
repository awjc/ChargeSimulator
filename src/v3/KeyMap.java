package v3;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class KeyMap {

  private Map<Object, String> nameMap = new HashMap<>();

  KeyMap() {

  }

  void initialize() throws IllegalAccessException {
    // for (Field f : KeyEvent.class.getDeclaredFields()) {
    //   if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
    //     f.setAccessible(true);
    //     nameMap.put(f.get(null), f.getName());
    //   }
    // }

    // for (Map.Entry<Object, String> x : nameMap.entrySet()) {
    //     //   System.out.println(x.getKey() + " -> " + x.getValue());
    //     // }
  }

}
