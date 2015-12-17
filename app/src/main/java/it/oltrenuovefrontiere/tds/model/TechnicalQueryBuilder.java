package it.oltrenuovefrontiere.tds.model;

/**
 * Created by Utente on 16/12/2015.
 */
public class TechnicalQueryBuilder {
    private String name;
    private String type;
    private String linea;

    public TechnicalQueryBuilder(String _name, String _type, String _linea) {
        this.name = _name;
        this.type = _type;
        this.linea = _linea;
    }

    public TechnicalQueryBuilder(String _name) {
        this(_name, "", "");
    }

    public TechnicalQueryBuilder(String _name, String _type) {
        this(_name, _type, "");
    }

    public TechnicalQueryBuilder() {
        this("", "", "");
    }

    public String getQueryString() {
        StringBuilder result = new StringBuilder();
        if (!this.name.isEmpty()) {
            result.append(DbAdapter.KEY_NAME + " like '%" + this.name + "%'");
        }
        if (!this.type.isEmpty()) {
            if (result.toString().isEmpty()) {
                result.append(DbAdapter.KEY_TYPE + " like '%" + this.type + "%'");
            } else {
                result.append(" AND " + DbAdapter.KEY_TYPE + " like '%" + this.type + "%'");
            }
        }
        if (!this.linea.isEmpty()) {
            if (result.toString().isEmpty()) {
                result.append(DbAdapter.KEY_LINEA + " like '%" + this.linea + "%'");
            } else {
                result.append(" AND " + DbAdapter.KEY_LINEA + " like '%" + this.linea + "%'");
            }
        }
        return result.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }
}
