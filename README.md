# Dynamic Configuration

Comcast Dynamic Configurattion - **dynocon** is on the mission of adapting the Configuration-as-a-Code concept.

**Dynocon** library allows your application to get access to the latest configuration changes without restarting.

JSON is a first-class citizen and supported out of the box as well as the traditional `.properties` format.

## Installation

```xml
<dependency>
	<groupId>com.comcast</groupId>
	<artifactId>dynocon-core</artifactId>
	<version>1.0.2</version>
</dependency>
```

## Example

If your JSON configuration looks like this:
```json
{
  "myPropertyName": "value1"
}
```
or you are using traditional `.properties` file:
```properties
myPropertyName = value1
```

You could always access the property as following:
```java
public static final Property<String> MY_PROPERTY = new Property<>("myPropertyName", String.class);
... SNIP ...
Assert.assertEquals("value1", MY_PROPERTY.get());
```
If the value in the file will be updated, you don't need to restart the application, `MY_PROPERTY.get()` will return the new value.

See [Wiki](https://github.com/Comcast/dynamic-configuration/wiki) for a full description of the **dynocon** library. 
