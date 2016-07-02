package org.exampledriven.cxf.webservice;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.exampledriven.cxfexample.data.LocationData;
import org.exampledriven.cxfexample.exception.DuplicateLocationException;
import org.exampledriven.cxfexample.exception.LocationNotFoundException;
import org.exampledriven.cxfexample.exception.meta.LocationResponseExceptionMapper;

public class LocationClient implements LocationService {

	public static enum CLIENT_TYPE {
		REST("REST"), SOAP("SOAP");

		public static CLIENT_TYPE fromString(final String value) {

			if (REST.value.equalsIgnoreCase(value)) {
				return REST;
			}

			if (SOAP.value.equalsIgnoreCase(value)) {
				return SOAP;
			}

			return null;
		}

		private String value;

		private CLIENT_TYPE(final String value) {
			this.value = value;
		}
	}

	private LocationService locationService;;

	public LocationClient(final String applicationURI, final CLIENT_TYPE clientType) {

		if (clientType == CLIENT_TYPE.REST) {
			final List<Object> providers = new LinkedList<Object>();
			providers.add(new LocationResponseExceptionMapper());
			this.locationService = JAXRSClientFactory.create(applicationURI + "/cxf/rest/", LocationService.class, providers, true);
			final ClientConfiguration cfgProxy = WebClient.getConfig(this.locationService);
			cfgProxy.getHttpConduit().getAuthorization().setPassword("restuser");
			cfgProxy.getHttpConduit().getAuthorization().setUserName("restuser");
		} else {
			final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(LocationService.class);
			factory.setAddress(applicationURI + "/cxf/soap/");
			factory.setUsername("restuser");
			factory.setPassword("restuser");
			this.locationService = (LocationService) factory.create();
		}

	}

	@Override
	public LocationData createLocation(final LocationData locationData) throws DuplicateLocationException {
		return this.locationService.createLocation(locationData);
	}

	@Override
	public void deleteAllLocation() {
		this.locationService.deleteAllLocation();
	}

	@Override
	public void deleteLocation(final String location) throws LocationNotFoundException {

		this.locationService.deleteLocation(location);

	}

	@Override
	public Collection<LocationData> readAllLocations() {
		return this.locationService.readAllLocations();
	}

	@Override
	public LocationData readLocation(final String location) throws LocationNotFoundException {
		return this.locationService.readLocation(location);
	}

	@Override
	public LocationData updateorCreateLocation(final LocationData locationData) {
		return this.locationService.updateorCreateLocation(locationData);
	}

}