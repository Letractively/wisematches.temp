package billiongoods.server.services.address;

import billiongoods.core.Personality;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AddressBookManager {
	AddressBook getAddressBook(Personality personality);


	AddressRecord addAddress(Personality personality, AddressRecord address);

	AddressRecord makePrimary(Personality personality, AddressRecord address);

	AddressRecord removeAddress(Personality personality, AddressRecord address);

	AddressRecord updateAddress(Personality principal, AddressRecord from, AddressRecord to);
}
