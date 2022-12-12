# Documentation

All class and their methods will be documentated here

## Front-end (View)

## Back-end (model)

### public class Clothes 

--- 

#### public enum 

Type:

* T_SHIRT
* LONG_SLEEVE
* SHORTS
* PANTS
* SHOES
* SWEATER
* LIGHT_JACKET
* HEAVY_JACKET

#### Members

Type type  

Color color  

boolean waterResistant  

private String uniqueId, imageURL  

Integer layer  

#### Methods 

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

---

### public class Clothes_Factory 

#### Public Methods

public T_shirt get_tshirt()

public Shirt get_long_sleeve()

publec Shoes get_shoes()

public Pants get_pants()

public Shorts get_shorts()

public LightJacket get_light_jacket()

public HeavyJacket get_heavyjacket()

public Sweater get_sweater()

---

### public class Color

#### Members

float red  

float green  

float blue  

float red2  

float green2  

float blue2  

String hex1  

String hex2  

ArrayList<Float> hsl1  

ArrayList<Float> hsl2  

#### Methods

public Color(float r, float g, float b, float r2, float g2, float b2, String hex1, String hex2)

public float getRed()

public float getGreen()

public float getBlue()

public float getRed2()

public float getGreen2()

public float getBlue2()

public String getHex1()

public String getHex2()

public ArrayList<Float> getHsl1() 

public ArrayList<Float> getHsl2() 

public ArrayList<Float> rgb2hsl(float r, float g, float b) 

public String toString()


---

### public class FireBaseManager

#### Members

private static FireBaseManager manager_instance

private final static String TAG = "FireBaseManager"

private FirebaseAuth mAuth

private Profile user

private int user_idx

private FirebaseDatabase database

private DatabaseReference myRef

private FirebaseUser currentUser

private String image_path

#### Methods

private FireBaseManager() 

public static FireBaseManager getInstance() 

public void setImagePath(String path)

public void setEnableCache(int value)

public void saveCache()

public void saveBitmap(Bitmap output, String fileName)

public void deleteCache()

public String getImagePath()

public Profile getProfile()

public void setProfile(Profile user)

public void addClothes(Clothes new_clothes)

public void updateEditedClothes() 

public void deleteItem(String uid) 

public void deleteOutFit(String uid) 

public User getUser()

public ArrayList<Clothes> getClothes()

public void updateOneLayerTemp(int temp)

public void updateThreeLayerTemp(int temp)

public void updateFlashMode(String temp)

public void updateTheme(int temp)

public void updateLanguage(String temp)

public int getUserIdx()

public void setUserIdx(int userIdx)

public void update()

public void addUser(User user)

public void deleteUser(int user_idx)

public boolean addOutfit(Outfit new_outfit)

public Wardrobe getWardrobe()

---

### public class Hash

#### Methods

public static String sha256(String s) 


---

### public class HeavyJacket extends Clothes

#### Methods

public HeavyJacket() 

public void setLayer(Integer layer) 


---

### public class LightJacket extends Clothes

#### Methods

public LightJacket() 

public void setLayer(Integer layer) 

---

### public class Outfit implements Comparable<Outfit> 

#### Members 

private ArrayList<String> clothes_uniqueIds

private String outfitUniqueId

private String name

#### Methods

public Outfit() 

public String getName()

public void setName(String name)

public String getOutfitUniqueId()

public void setOutfitUniqueId(String id)

public ArrayList<String> getOutfit()

public void setOutfit(ArrayList<String> outfit)

public void addClothesToOutfit(String clothingId)

public int getSize()

public int compareTo(Outfit outfit) 

---

### public class Pants extends Clothes 

#### Methods

public Pants() 

public void setLayer(Integer layer) 

---

### public class Profile 

#### Members

private ArrayList<User> users

private String accountEmail

private String userId

private final static String TAG = "Profile"

#### Methods

public Profile(String accountEmail, String userId)

public ArrayList<User> getUsers()

public String getAccountEmail()

public String getUserId()

public void setUsers(ArrayList<User> users)

public void setAccountEmail(String email)

public void setUserId(String id)

public void addUser(User user)

public void deleteUser(int user_idx)

public String toString()

---

### public class RandomString

#### Methods

public static String getAlphaNumericString(int n)

public static void main(String[] args)    

---

### public class Shirt extends Clothes

#### Methods

public Shirt() 

public void setLayer(Integer layer) 

---

### public class Shoes extends Clothes

#### Methods

public Shoes() 

---

### public class Shorts extends Clothes

#### Methods 

public Shorts()

public void setLayer(Integer layer) 

---

### public class Sweater extends Clothes

#### Methods

public Sweater()

public void setLayer(Integer layer) 

---

### public class T_shirt extends Clothes

#### Methods

public T_shirt() 

public void setLayer(Integer layer) 

---

### public class User 

#### Members

private String userId, username, password

private Wardrobe wardrobe

private boolean passwordProtected

private User_settings user_settings

#### Methods

public User(String userId, String username)

public User(String userId, String username, Wardrobe wardrobe, boolean password_protected)

public User(String userId, String username, Wardrobe wardrobe, boolean password_protected, String password)

public String getUserId()

public String getUsername()

public Wardrobe getWardrobe()

public boolean getPasswordProtected()

public String getPassword()

public void setPassword(String password)

public User_settings getUserSettings()

public void setUserSettings(User_settings settings)

public boolean setUserId(String userId)

public boolean setUsername(String username)

public boolean setWardrobe(Wardrobe wardrobe)

public boolean setPasswordProtected(boolean password_protected)

public String toString()

---

### public class User_settings 

#### Members

int oneLayerTemp, threeLayerTemp, theme, enableCache

String flashMode, language

#### Methods

public void setOneLayerTemp(int temp)

public void setThreeLayerTemp(int temp)

public void setFlashMode(String temp) 

public void setTheme(int temp) 

public void setLanguage(String lang) 

public void setEnableCache(int enable)

public int getOneLayerTemp()

public int getThreeLayerTemp()

public String getFlashMode() 

public int getTheme() 

public String getLanguage() 

public int getEnableCache() 

