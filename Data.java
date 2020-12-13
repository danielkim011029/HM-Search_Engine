/*
A Data object that represents all the information a clothing has
*/
public class Data {
    public String inventoryName="";
    public String username="";
    public String name="";
    public String color="";
    public String description="";
    public String price="";
    public String image="";

    public Data(){
        username="";
        name="";
        color="";
        description="";
        price="";
    }

    public void setInventoryName(String inventoryName){
        this.inventoryName=inventoryName;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setColor(String color){
        this.color=color;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public void setPrice(String price){
        this.price=price;
    }

    public void setImage(String image){
        this.image=image;
    }

    public String getInventoryName(){
        return inventoryName;
    }

    public String getUsername(){
        return username;
    }
    public String getName(){
        return name;
    }

    public String getColor(){
        return color;
    }

    public String getPrice(){
        return price;
    }

    public String getImage(){
        return image;
    }
    
    public void print(){
        System.out.println("Inventory Number: "+inventoryName+"\n"
                            +"username: "+username+"\n"
                            +"name: "+name+"\n"
                            +"color: "+color+"\n"
                            +"description: "+description+"\n"
                            +"price: "+price+"\n");
    }

    public String getInfo(){
        String output="";
        output+="Inventory Number: "+inventoryName+"\n"
                +"username: "+username+"\n"
                +"name: "+name+"\n"
                +"color: "+color+"\n"
                +"description: "+description+"\n"
                +"price: "+price+"\n";
        return output;
    }
}
