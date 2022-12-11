# Documentation

All class and their methods will be documentated here

## Front-end (View)

## Back-end (model)

public class Clothes 

--- 

public enum Type:

* T_SHIRT
* LONG_SLEEVE
* SHORTS
* PANTS
* SHOES
* SWEATER
* LIGHT_JACKET
* HEAVY_JACKET

--- 

members

Type type
Color color
boolean waterResistant

private String uniqueId, imageURL

Integer layer

---

public Clothes(Type type, Color color, String imageURL, boolean waterResistant, Integer layer)

public Type getType()

public boolean isWaterResistant()

public Color getColor()

public String getUniqueId()

public String getImageURL()
    
public Integer getLayer() 

public void setType(Type type)

public void setWaterResistant(Boolean isResistant)

public void setColor(Color color)

public void setUniqueId(String id)

public void setImageURL(String image)
    
public void setLayer(Integer layer) 

public String toString()

public static String[] getTypes(Class<? extends Type> e) 

