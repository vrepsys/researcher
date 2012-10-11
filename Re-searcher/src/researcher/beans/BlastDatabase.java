package researcher.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "blast_databases")
public class BlastDatabase {

    private long id;

    private String path;

    private String name;

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlastDatabase))
            return false;
        BlastDatabase db = (BlastDatabase) obj;
        if (name == null && db.name != null)
            return false;
        if (db.name == null && name != null)
            return false;
        if (name != null && db.name != null && !name.equals(db.name))
            return false;

        if (path == null && db.path != null)
            return false;
        if (db.path == null && path != null)
            return false;
        if (path != null && db.path != null && !path.equals(db.path))
            return false;
        return true;
    }

}
