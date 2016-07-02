package org.exampledriven.cxfexample.mvc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.exampledriven.cxf.webservice.LocationClient;
import org.exampledriven.cxfexample.data.LocationData;
import org.exampledriven.cxfexample.exception.DuplicateLocationException;
import org.exampledriven.cxfexample.exception.LocationNotFoundException;
import org.exampledriven.cxfexample.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/")
@SessionAttributes("locationData")
public class LocationClientController {

	private final Logger	logger	= LoggerFactory.getLogger(this.getClass());
	private String			applicationURI;

	private LocationClient	locationClientSOAP;
	private LocationClient	locationClientREST;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createLocation(@RequestParam final String protocol, final LocationData locationData, final Model model, final HttpServletRequest request,
			final BindingResult bindingResult) throws DuplicateLocationException {

		this.logger.debug("location param is " + locationData);

		try {
			final LocationData locationDataResp = this.getLocationClient(request, LocationClient.CLIENT_TYPE.fromString(protocol)).createLocation(locationData);
			final List<LocationData> locationDataList = new LinkedList<LocationData>();
			locationDataList.add(locationDataResp);
			model.addAttribute("locationDataList", locationDataList);
			this.logger.debug("location result is " + locationDataResp);
		} catch (final ValidationException e) {
			for (final String field : e.getExceptionData().getData().keySet()) {
				bindingResult.rejectValue(field, null, e.getExceptionData().getData().get(field));
			}
		}

		return "locationClient";

	}

	@RequestMapping(value = "/deleteall", method = RequestMethod.GET)
	public String deleteAllLocation(@RequestParam final String protocol, final Model model, final HttpServletRequest request)
			throws DuplicateLocationException {
		this.getLocationClient(request, LocationClient.CLIENT_TYPE.fromString(protocol)).deleteAllLocation();

		return "locationClient";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String deleteAllLocation(@RequestParam final String protocol, @RequestParam final String location, final Model model,
			final HttpServletRequest request) throws DuplicateLocationException, LocationNotFoundException {
		this.getLocationClient(request, LocationClient.CLIENT_TYPE.fromString(protocol)).deleteLocation(location);

		return "locationClient";
	}

	@RequestMapping(value = "/readall", method = RequestMethod.GET)
	public String readAllLocation(@RequestParam final String protocol, final Model model, final HttpServletRequest request) throws DuplicateLocationException {
		final Collection<LocationData> locationDataList = this.getLocationClient(request, LocationClient.CLIENT_TYPE.fromString(protocol)).readAllLocations();

		model.addAttribute("locationDataList", locationDataList);

		return "locationClient";
	}

	@RequestMapping(value = "/read", method = RequestMethod.GET)
	public String readLocation(@RequestParam final String protocol, @RequestParam("location") final String location, final Model model,
			final HttpServletRequest request) throws LocationNotFoundException {

		this.logger.debug("location param is " + location);

		final LocationData locationDataResp = this.getLocationClient(request, LocationClient.CLIENT_TYPE.fromString(protocol)).readLocation(location);

		this.logger.debug("location result is " + locationDataResp);

		final List<LocationData> locationDataList = new LinkedList<LocationData>();
		locationDataList.add(locationDataResp);
		model.addAttribute("locationDataList", locationDataList);

		return "locationClient";

	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(final Model model) {

		model.addAttribute("locationData", new LocationData());

		return "locationClient";

	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateLocation(@RequestParam final String protocol, final LocationData locationData, final Model model, final HttpServletRequest request)
			throws DuplicateLocationException {

		this.logger.debug("location param is " + locationData);

		final LocationData locationDataResp = this.getLocationClient(request, LocationClient.CLIENT_TYPE.fromString(protocol)).updateorCreateLocation(
				locationData);
		/*
		 * new JAXBElement<LocationData>();
		 * LocationData locationDataResp = locationClient.createLocation(locationData.getLocation(), locationData);
		 */

		this.logger.debug("location result is " + locationDataResp);

		final List<LocationData> locationDataList = new LinkedList<LocationData>();
		locationDataList.add(locationDataResp);
		model.addAttribute("locationDataList", locationDataList);

		return "locationClient";

	}

	/**
	 * returns the URI of the application
	 * 
	 * @param req
	 * @return
	 */
	private String getApplicationURI(final HttpServletRequest req) {
		if (this.applicationURI == null) {
			String port;

			if ("http".equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 80
					|| "https".equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 443) {
				port = ":" + req.getServerPort();
			} else {
				port = "";
			}

			this.applicationURI = req.getScheme() + "://" + req.getServerName() + port + req.getContextPath();

			this.logger.debug("Application URL was set to " + this.applicationURI);
		}

		return this.applicationURI;
	}

	private LocationClient getLocationClient(final HttpServletRequest request, final LocationClient.CLIENT_TYPE clientType) {

		if (clientType.equals(LocationClient.CLIENT_TYPE.SOAP)) {
			if (this.locationClientSOAP == null) {
				this.locationClientSOAP = new LocationClient(this.getApplicationURI(request), LocationClient.CLIENT_TYPE.SOAP);
			}

			return this.locationClientSOAP;
		}

		if (clientType.equals(LocationClient.CLIENT_TYPE.REST)) {
			if (this.locationClientREST == null) {
				this.locationClientREST = new LocationClient(this.getApplicationURI(request), LocationClient.CLIENT_TYPE.REST);
			}

			return this.locationClientREST;
		}

		return null;
	}

}
