package billiongoods.server.web.servlet.sdo;

import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * {
 * "sEcho":1,
 * "iColumns":7,
 * "sColumns":"",
 * "iDisplayStart":0,
 * "iDisplayLength":10,
 * "amDataProp":[0,1,2,3,4,5,6],
 * "sSearch":"",
 * "bRegex":false,
 * "asSearch":["","","","","","",""],
 * "abRegex":[false,false,false,false,false,false,false],
 * "abSearchable":[true,true,true,true,true,true,true],
 * "iSortingCols":1,
 * "aiSortCol":[0],
 * "asSortDir":["asc"],
 * "abSortable":[true,true,true,true,true,true,true]
 * }
 *
 * @see "http://java-juice.tumblr.com/post/13102234732/datatables-spring-mvc-integration"
 */
public class DataTablesRequest implements Serializable {
    @JsonProperty(value = "sEcho")
    public String echo;

    @JsonProperty(value = "iColumns")
    public int numColumns;

    @JsonProperty(value = "sColumns")
    public String columns;

    @JsonProperty(value = "iDisplayStart")
    public int displayStart;

    @JsonProperty(value = "iDisplayLength")
    public int displayLength;

    @JsonProperty(value = "sSearch")
    public String searchQuery;

    @JsonProperty(value = "asSearch")
    public String[] columnSearches;

    @JsonProperty(value = "bRegex")
    public boolean hasRegex;

    @JsonProperty(value = "abRegex")
    public boolean[] regexColumns;

    @JsonProperty(value = "abSearchable")
    public boolean[] searchColumns;

    @JsonProperty(value = "iSortingCols")
    public int sortingCols;

    @JsonProperty(value = "aiSortCol")
    public int[] sortedColumns;

    @JsonProperty(value = "asSortDir")
    public String[] sortDirections;

    @JsonProperty(value = "abSortable")
    public boolean[] sortableColumns;

    @JsonProperty(value = "amDataProp")
    public String[] dataProp;

    public DataTablesRequest() {
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String[] getColumnSearches() {
        return columnSearches;
    }

    public Orders getOrders() {
        if (sortingCols == 0) {
            return null;
        }
        final Order[] orders = new Order[sortingCols];
        for (int i = 0; i < sortingCols; i++) {
            final int index = sortedColumns[i];
            final String name = dataProp[index];
            final String direction = sortDirections[i];
            orders[i] = "asc".equals(direction) ? Order.asc(name) : Order.desc(name);
        }
        return Orders.all(orders);
    }

    public Range getLimit() {
        return Range.limit(displayStart, displayLength);
    }

    public DataTablesResponse replay(int totalCount, List<?> rows) {
        return new DataTablesResponse(totalCount, rows, this);
    }
}
