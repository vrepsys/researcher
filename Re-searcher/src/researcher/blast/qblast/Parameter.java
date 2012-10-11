package researcher.blast.qblast;

public class Parameter implements Comparable<Parameter>{

    private String name;

    private String value;

    public Parameter(String key, String value) {
        if (key == null || value == null)
//            throw new NullPointerException("key=" + key + " value=" + value);
        	System.out.println("key=" + key + " value=" + value);
        this.name = key;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }

   public int compareTo(Parameter o) {
      int cmp = String.CASE_INSENSITIVE_ORDER.compare(this.name, o.name);
      if (cmp != 0) return cmp;
      return String.CASE_INSENSITIVE_ORDER.compare(this.value, o.value);
   }
   
   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Parameter)) return false;
      if (obj == null) return false;
      return this.getName().equals(((Parameter)obj).getName());
   }

}
