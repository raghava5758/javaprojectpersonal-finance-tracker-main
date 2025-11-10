package models;

public class Category {
    private String name;
    private String type; // "Income" or "Expense"
    
    public Category(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return name.equals(category.name) && type.equals(category.type);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode();
    }
}


