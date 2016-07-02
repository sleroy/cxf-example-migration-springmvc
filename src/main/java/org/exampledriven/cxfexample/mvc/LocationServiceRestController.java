package org.exampledriven.cxfexample.mvc;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.PathParam;

import org.exampledriven.cxf.webservice.LocationServiceEndpoint;
import org.exampledriven.cxfexample.data.LocationData;
import org.exampledriven.cxfexample.exception.DuplicateLocationException;
import org.exampledriven.cxfexample.exception.LocationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RequestMapping("/rest/location/")
@RestController
@Api(value = "Location Service Rest Controller")
public class LocationServiceRestController {

	@Autowired
	private LocationServiceEndpoint locationServiceEndPoint;

	@RequestMapping(method = RequestMethod.POST)

	@ApiOperation(value = "createLocation stores a new location data and returns the newly created location data")
	public LocationData createLocation(@ApiParam("new location data") @Valid final LocationData locationData) throws DuplicateLocationException {
		return this.locationServiceEndPoint.createLocation(locationData);
	}

	@RequestMapping(value = "*", method = RequestMethod.DELETE)
	@ApiOperation(value = "deleteAllLocation deletes All location data")
	public void deleteAllLocation() {
		this.locationServiceEndPoint.deleteAllLocation();
	}

	@RequestMapping(value = "{location}", method = RequestMethod.DELETE)
	@ApiOperation(value = "deleteLocation deletes a location data and returns the location data")
	public void deleteLocation(
			@ApiParam(value = "the string representation of the location") @PathParam("location") @NotNull @Size(max = 10, min = 5) final String location)
			throws LocationNotFoundException {
		this.locationServiceEndPoint.deleteLocation(location);
	}

	@RequestMapping(value = "*", method = RequestMethod.GET)
	@ApiOperation(value = "readAllLocations returns all locations and returns the location data")
	public Collection<LocationData> readAllLocations() {
		return this.locationServiceEndPoint.readAllLocations();
	}

	@RequestMapping(value = "{location}", method = RequestMethod.GET)
	@ApiOperation(value = "readLocation returns a location data and returns the location data")
	public LocationData readLocation(
			@ApiParam(value = "the string representation of the location") @PathParam("location") @NotNull @Size(max = 10, min = 5) final String location)
			throws LocationNotFoundException {
		return this.locationServiceEndPoint.readLocation(location);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ApiOperation("updateOrCreateLocation updates or creates a new location data and returns the newly created location data")
	public LocationData updateOrCreateLocation(@Valid final LocationData locationData) {
		return this.locationServiceEndPoint.updateorCreateLocation(locationData);
	}

}
