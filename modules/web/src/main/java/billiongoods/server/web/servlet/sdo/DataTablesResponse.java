package billiongoods.server.web.servlet.sdo;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DataTablesResponse {
    private final int totalCount;
    private final List<?> rows;
    private final DataTablesRequest request;

    public DataTablesResponse(int totalCount, List<?> rows, DataTablesRequest request) {
        this.totalCount = totalCount;
        this.rows = rows;
        this.request = request;
    }

    @JsonProperty("iEcho")
    public Object getEcho() {
        return request.echo;
    }

    @JsonProperty("aaData")
    public List<?> getRows() {
        return rows;
    }

    @JsonProperty("iTotalRecords")
    public int getTotalRecords() {
        return totalCount;
    }

    @JsonProperty("iTotalDisplayRecords")
    public int getTotalDisplayRecords() {
        return totalCount;
    }
}
