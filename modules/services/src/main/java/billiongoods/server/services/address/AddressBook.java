package billiongoods.server.services.address;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AddressBook {
	AddressRecord getPrimary();

	List<AddressRecord> getAddresses();

	AddressRecord getAddressRecord(Integer id);
}
