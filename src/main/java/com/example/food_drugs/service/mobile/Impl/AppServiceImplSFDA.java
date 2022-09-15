package com.example.food_drugs.service.mobile.Impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import com.example.food_drugs.dto.responses.mobile.InventoryLastDataResponse;
import com.example.food_drugs.dto.responses.mobile.WareHouseInvLastDataResponse;
import com.example.food_drugs.entity.*;
import com.example.food_drugs.repository.*;
import com.example.food_drugs.service.mobile.AppServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.DriverServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.GroupsServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.dto.DeviceTempHum;
import com.example.food_drugs.photo.DecodePhotoSFDA;

@Component
@Service
public class AppServiceImplSFDA extends RestServiceController implements AppServiceSFDA {

	private static final Log logger = LogFactory.getLog(AppServiceImplSFDA.class);
	private GetObjectResponse getObjectResponse;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private UserRoleServiceImpl userRoleServiceImpl;

	@Autowired
	private DeviceServiceImpl deviceServiceImpl;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private MongoInventoryLastDataRepository mongoInventoryLastDataRepository;

	@Autowired
	private DriverServiceImpl driverServiceImpl;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private DriverRepositorySFDA driverRepositorySFDA;

	@Autowired
	private UserClientDriverRepository userClientDriverRepository;

	@Autowired
	private GeofenceRepository geofenceRepository;

	@Autowired
	private GeofenceRepositorySFDA geofenceRepositorySFDA;

	@Autowired
	private GeofenceServiceImpl geofenceServiceImpl;

	@Autowired
	private UserClientGeofenceRepository userClientGeofenceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	UserClientWarehouseRepository userClientWarehouseRepository;

	@Autowired
	private WarehousesRepository warehousesRepository;

	@Autowired
	private UserClientInventoryRepository userClientInventoryRepository;

	@Autowired
	private MongoInventoryNotificationRepo mongoInventoryNotificationRepo;

	@Autowired
	private MongoInventoryLastDataRepo mongoInventoryLastDataRepo;


	@Autowired
	private UserClientDeviceRepository userClientDeviceRepository;

	@Autowired
	private GroupRepository groupRepository;


	@Autowired
	private UserClientGroupRepository userClientGroupRepository;

	@Autowired
	private GroupsServiceImpl groupsServiceImpl;

	@Autowired
	private MongoPositionRepoSFDA mongoPositionRepoSFDA;

	@Override
	public ResponseEntity<?> activeDeviceAppSFDA(String TOKEN, Long userId, Long deviceId) {
		logger.info("************************ activeDevice ENDED ***************************");
		if (TOKEN.equals("")) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", devices);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (deviceId.equals(0) || userId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID and Device ID are Required", devices);
			logger.info("************************ activeDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found", devices);
			logger.info("************************ activeDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (!loggedUser.getAccountType().equals(1)) {
			if (!userRoleServiceImpl.checkUserHasPermission(userId, "DEVICE", "delete")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete device", null);
				logger.info("************************ activeDevice ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		Device device = deviceRepository.findOne(deviceId);
		if (device == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found", devices);
			logger.info("************************ activeDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		} else {
			boolean isParent = false;
			User creater = userServiceImpl.findById(userId);
			if (creater == null) {
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found", devices);
				logger.info("************************ activeDevice ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			if (creater.getAccountType().equals(4)) {
				Set<User> parentClient = creater.getUsersOfUser();
				if (parentClient.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ", null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				} else {

					User parent = null;
					for (User object : parentClient) {
						parent = object;
					}
					Set<User> deviceParent = device.getUser();
					if (deviceParent.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ", null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					} else {

						for (User deviceUser : deviceParent) {
							if (deviceUser.getId().equals(parent.getId())) {

								isParent = true;
								break;
							}
						}
					}
				}
			}
			if (!deviceServiceImpl.checkIfParent(device, creater) && !isParent) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this user ", null);
				logger.info("************************ editDevice ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

			device.setDeleteDate(null);
			deviceRepository.save(device);

			List<Device> devices = null;


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", devices);
			logger.info("************************ activeDevice ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
	}


	@Override
	public ResponseEntity<?> activeDriverApp(String TOKEN, Long driverId, Long userId) {

		logger.info("************************ activeDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN  is required", drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (userId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required", drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser  is not Found", drivers);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (!loggedUser.getAccountType().equals(1)) {
			if (!userRoleService.checkUserHasPermission(userId, "DRIVER", "delete")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active driver", null);
				logger.info("************************ activeDriver ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (!driverId.equals(0)) {
			Driver driver = driverRepository.findOne(driverId);
			if (driver != null) {
				boolean isParent = false;
				if (loggedUser.getAccountType().equals(4)) {
					Set<User> parentClients = loggedUser.getUsersOfUser();
					if (parentClients.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this driver", drivers);
						return ResponseEntity.badRequest().body(getObjectResponse);
					} else {
						User parent = null;
						for (User object : parentClients) {
							parent = object;
						}
						Set<User> driverParent = driver.getUserDriver();
						if (driverParent.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this driver", drivers);
							return ResponseEntity.badRequest().body(getObjectResponse);
						} else {
							for (User parentObject : driverParent) {
								if (parentObject.getId().equals(parent.getId())) {
									isParent = true;
									break;
								}
							}
						}
					}
				}
				if (!driverServiceImpl.checkIfParent(driver, loggedUser) && !isParent) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this driver ", null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

				driver.setDelete_date(null);
				driverRepository.save(driver);


				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", drivers);
				logger.info("************************ activeDriver ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);


			} else {

				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found", drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required", drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}


	}


	@Override
	public ResponseEntity<?> getAllDriversAppSFDA(String TOKEN, Long id, int offset, String search, int active, String exportData) {

		logger.info("************************ getAllDrivers STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();
		List<CustomDriverList> customDrivers = new ArrayList<CustomDriverList>();
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (!user.getAccountType().equals(1)) {
					if (!userRoleService.checkUserHasPermission(id, "DRIVER", "list")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver list", null);
						logger.info("************************ getAllUserDrivers ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				userServiceImpl.resetChildernArray();
				if (user.getAccountType().equals(4)) {

					List<Long> driverIds = userClientDriverRepository.getDriverIds(id);
					Integer size = 0;
					if (driverIds.size() > 0) {

						if (active == 0) {

							if (exportData.equals("exportData")) {
								customDrivers = driverRepositorySFDA.getAllDriversCustomByIdsDeactiveExport(driverIds, search);

							} else {
								customDrivers = driverRepositorySFDA.getAllDriversCustomByIdsDeactive(driverIds, offset, search);
								size = driverRepositorySFDA.getAllDriversSizeByIdsDeactive(driverIds, search);
							}


						}

						if (active == 2) {
							if (exportData.equals("exportData")) {
								customDrivers = driverRepositorySFDA.getAllDriversCustomByIdsAllExport(driverIds, search);
							} else {
								customDrivers = driverRepositorySFDA.getAllDriversCustomByIdsAll(driverIds, offset, search);
								size = driverRepositorySFDA.getAllDriversSizeByIdsAll(driverIds, search);
							}

						}

						if (active == 1) {
							if (exportData.equals("exportData")) {
								customDrivers = driverRepository.getAllDriversCustomByIdsExport(driverIds, search);

							} else {

								customDrivers = driverRepository.getAllDriversCustomByIds(driverIds, offset, search);
								size = driverRepository.getAllDriversSizeByIds(driverIds, search);

							}

						}
					}

					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", customDrivers, size);
					logger.info("************************ getAllDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				}
				List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
				List<Long> usersIds = new ArrayList<>();
				if (childernUsers.isEmpty()) {
					usersIds.add(id);
				} else {
					usersIds.add(id);
					for (User object : childernUsers) {
						usersIds.add(object.getId());
					}
				}
				Integer size = 0;
				if (active == 0) {
					if (exportData.equals("exportData")) {
						customDrivers = driverRepositorySFDA.getAllDriversCustomDeactiveExport(usersIds, search);

					} else {
						customDrivers = driverRepositorySFDA.getAllDriversCustomDeactive(usersIds, offset, search);
						size = driverRepositorySFDA.getAllDriversSizeDeactive(usersIds, search);
					}


				}

				if (active == 2) {
					if (exportData.equals("exportData")) {
						customDrivers = driverRepositorySFDA.getAllDriversCustomAllExport(usersIds, search);

					} else {
						customDrivers = driverRepositorySFDA.getAllDriversCustomAll(usersIds, offset, search);
						size = driverRepositorySFDA.getAllDriversSizeAll(usersIds, search);
					}


				}

				if (active == 1) {
					if (exportData.equals("exportData")) {
						customDrivers = driverRepository.getAllDriversCustomExport(usersIds, search);

					} else {

						customDrivers = driverRepository.getAllDriversCustom(usersIds, offset, search);
						size = driverRepository.getAllDriversSize(usersIds, search);
					}


				}
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", customDrivers, size);
				logger.info("************************ getAllDrivers ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);


			}

		} else {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}


	}


	@Override
	public ResponseEntity<?> activeGeofenceApp(String TOKEN, Long geofenceId, Long userId) {

		logger.info("************************ activeGeofence STARTED ***************************");


		List<Geofence> geofences = new ArrayList<Geofence>();
		User user = userServiceImpl.findById(userId);
		if (user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", geofences);
			return ResponseEntity.status(404).body(getObjectResponse);

		}

		if (user.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "GEOFENCE", "delete")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active", null);
				logger.info("************************ activeGeofence ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (geofenceId != 0) {
			Geofence geofence = geofenceRepository.findOne(geofenceId);
			if (geofence != null) {

				boolean isParent = false;
				if (user.getAccountType().equals(4)) {
					Set<User> parentClients = user.getUsersOfUser();
					if (parentClients.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this geofnece", geofences);
						return ResponseEntity.badRequest().body(getObjectResponse);
					} else {
						User parent = null;
						for (User object : parentClients) {
							parent = object;
						}
						Set<User> geofneceParent = geofence.getUserGeofence();
						if (geofneceParent.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this geofnece", geofences);
							return ResponseEntity.badRequest().body(getObjectResponse);
						} else {
							for (User parentObject : geofneceParent) {
								if (parentObject.getId().equals(parent.getId())) {
									isParent = true;
									break;
								}
							}
						}
					}

				}
				if (!geofenceServiceImpl.checkIfParent(geofence, user) && !isParent) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this geofnece ", geofences);
					logger.info("************************ activeGeofence ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

				geofence.setDelete_date(null);
				geofenceRepository.save(geofence);


				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", geofences);
				logger.info("************************ activeGeofence ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);


			} else {

				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was not found", geofences);
				return ResponseEntity.status(404).body(getObjectResponse);

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required", geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}


	}

	@Override
	public ResponseEntity<?> getAllGeofencesAppSFDA(String TOKEN, Long id, int offset, String search, int active, String exportData) {

		logger.info("************************ getAllUserGeofences STARTED ***************************");

		List<Geofence> geofences = new ArrayList<Geofence>();

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (id != 0) {

			User user = userServiceImpl.findById(id);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", geofences);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(id, "GEOFENCE", "list")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get geofences list", null);
						logger.info("************************ getAllUserDevices ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {

					userServiceImpl.resetChildernArray();
					if (user.getAccountType().equals(4)) {


						List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(id);
						Integer size = 0;
						if (geofenceIds.size() > 0) {

							if (active == 0) {

								if (exportData.equals("exportData")) {
									geofences = geofenceRepositorySFDA.getAllGeofencesByIdsDeactiveExport(geofenceIds, search);
								} else {
									geofences = geofenceRepositorySFDA.getAllGeofencesByIdsDeactive(geofenceIds, offset, search);
									size = geofenceRepositorySFDA.getAllGeofencesSizeByIdsDeactive(geofenceIds, search);
								}


							}

							if (active == 2) {

								if (exportData.equals("exportData")) {
									geofences = geofenceRepositorySFDA.getAllGeofencesByIdsAllExport(geofenceIds, search);
								} else {
									geofences = geofenceRepositorySFDA.getAllGeofencesByIdsAll(geofenceIds, offset, search);
									size = geofenceRepositorySFDA.getAllGeofencesSizeByIdsAll(geofenceIds, search);
								}


							}

							if (active == 1) {

								if (exportData.equals("exportData")) {
									geofences = geofenceRepository.getAllGeofencesByIdsExport(geofenceIds, search);

								} else {
									geofences = geofenceRepository.getAllGeofencesByIds(geofenceIds, offset, search);
									size = geofenceRepository.getAllGeofencesSizeByIds(geofenceIds, search);
								}


							}
						}
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", geofences, size);
						logger.info("************************ getAllUserGeofences ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
					List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					List<Long> usersIds = new ArrayList<>();
					if (childernUsers.isEmpty()) {
						usersIds.add(id);
					} else {
						usersIds.add(id);
						for (User object : childernUsers) {
							usersIds.add(object.getId());
						}
					}


					Integer size = 0;

					if (active == 0) {
						if (exportData.equals("exportData")) {
							geofences = geofenceRepositorySFDA.getAllGeofencesDeactiveExport(usersIds, search);

						} else {
							geofences = geofenceRepositorySFDA.getAllGeofencesDeactive(usersIds, offset, search);
							size = geofenceRepositorySFDA.getAllGeofencesSizeDeactive(usersIds, search);
						}


					}

					if (active == 2) {
						if (exportData.equals("exportData")) {
							geofences = geofenceRepositorySFDA.getAllGeofencesAllExport(usersIds, search);

						} else {
							geofences = geofenceRepositorySFDA.getAllGeofencesAll(usersIds, offset, search);
							size = geofenceRepositorySFDA.getAllGeofencesSizeAll(usersIds, search);
						}


					}

					if (active == 1) {
						if (exportData.equals("exportData")) {
							geofences = geofenceRepository.getAllGeofencesExport(usersIds, search);

						} else {
							geofences = geofenceRepository.getAllGeofences(usersIds, offset, search);
							size = geofenceRepository.getAllGeofencesSize(usersIds, search);
						}


					}


					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", geofences, size);
					logger.info("************************ getAllUserGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", geofences);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}


	}

	@Override
	public ResponseEntity<?> getInventoriesListApp(String TOKEN, Long id, int offset, String search, int active, String exportData) {
		logger.info("************************ getInventoriesList STARTED ***************************");
		Integer size = 0;
		List<Map> data = new ArrayList<>();
		List<Inventory> inventories = new ArrayList<Inventory>();

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (id != 0) {

			User user = userServiceImpl.findById(id);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", inventories);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(id, "INVENTORY", "list")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get INVENTORY list", null);
						logger.info("************************ getInventoryList ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {
					List<Long> usersIds = new ArrayList<>();

					if (user.getAccountType().equals(4)) {
						List<Long> inventoryIds = userClientInventoryRepository.getInventoryIds(id);
						if (inventoryIds.size() > 0) {
							if (active == 0) {
								if (exportData.equals("exportData")) {
									inventories = inventoryRepository.getInventoriesByIdsDeactiveExport(inventoryIds, search);

								} else {
									inventories = inventoryRepository.getInventoriesByIdsDeactive(inventoryIds, offset, search);
									size = inventoryRepository.getInventoriesSizeByIdsDeactive(inventoryIds);
								}


							}

							if (active == 2) {
								if (exportData.equals("exportData")) {
									inventories = inventoryRepository.getInventoriesByIdsAllExport(inventoryIds, search);


								} else {
									inventories = inventoryRepository.getInventoriesByIdsAll(inventoryIds, offset, search);
									size = inventoryRepository.getInventoriesSizeByIdsAll(inventoryIds);
								}


							}

							if (active == 1) {
								if (exportData.equals("exportData")) {
									inventories = inventoryRepository.getInventoriesByIdsExport(inventoryIds, search);

								} else {
									inventories = inventoryRepository.getInventoriesByIds(inventoryIds, offset, search);
									size = inventoryRepository.getInventoriesSizeByIds(inventoryIds);
								}


							}

						}
					} else {
						List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
						if (childernUsers.isEmpty()) {
							usersIds.add(id);
						} else {
							usersIds.add(id);
							for (User object : childernUsers) {
								usersIds.add(object.getId());
							}
						}


						if (active == 0) {
							if (exportData.equals("exportData")) {
								inventories = inventoryRepository.getInventoriesDeactiveExport(usersIds, search);

							} else {
								inventories = inventoryRepository.getInventoriesDeactive(usersIds, offset, search);
								size = inventoryRepository.getInventoriesSizeDeactive(usersIds);
							}


						}

						if (active == 2) {
							if (exportData.equals("exportData")) {
								inventories = inventoryRepository.getInventoriesAllExport(usersIds, search);

							} else {
								inventories = inventoryRepository.getInventoriesAll(usersIds, offset, search);
								size = inventoryRepository.getInventoriesSizeAll(usersIds);
							}


						}

						if (active == 1) {
							if (exportData.equals("exportData")) {
								inventories = inventoryRepository.getInventoriesExport(usersIds, search);

							} else {
								inventories = inventoryRepository.getInventories(usersIds, offset, search);
								size = inventoryRepository.getInventoriesSize(usersIds);
							}


						}
					}


					for (Inventory inventory : inventories) {
						Map InventoriesList = new HashMap();


						InventoriesList.put("id", inventory.getId());
						InventoriesList.put("trackerIMEI", inventory.getTrackerIMEI());
						InventoriesList.put("inventoryNumber", inventory.getInventoryNumber());
						InventoriesList.put("name", inventory.getName());
						InventoriesList.put("storingCategory", inventory.getStoringCategory());
						InventoriesList.put("userId", inventory.getUserId());
						InventoriesList.put("delete_date", inventory.getDeleteDate());
						InventoriesList.put("warehouseId", inventory.getWarehouseId());
						InventoriesList.put("referenceKey", inventory.getReferenceKey());
						InventoriesList.put("protocolType", inventory.getProtocolType());
						InventoriesList.put("userName", null);
						InventoriesList.put("warehouserName", null);

						InventoriesList.put("create_date", inventory.getCreate_date());
						InventoriesList.put("regestration_to_elm_date", inventory.getRegestration_to_elm_date());
						InventoriesList.put("delete_from_elm_date", inventory.getDelete_from_elm_date());
						InventoriesList.put("update_date_in_elm", inventory.getUpdate_date_in_elm());

						Warehouse war = new Warehouse();
						User us = new User();

						if (inventory.getWarehouseId() != null) {
							war = warehousesRepository.findOne(inventory.getWarehouseId());

						}

						if (inventory.getUserId() != null) {
							us = userRepository.findOne(inventory.getUserId());

						}
						if (us != null) {
							InventoriesList.put("userName", us.getName());

						}
						if (war != null) {
							InventoriesList.put("warehouserName", war.getName());

						}
						data.add(InventoriesList);


					}
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", data, size);
					logger.info("************************ getInventoriesList ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", inventories);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> createInventoriesApp(String TOKEN, Inventory inventory, Long userId) {
		logger.info("************************ createInventories STARTED ***************************");
		Date now = new Date();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = isoFormat.format(now);

		inventory.setCreate_date(nowTime);
		inventory.setDeleteDate(null);

		List<Inventory> inventories = new ArrayList<Inventory>();
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (userId != 0) {
			User user = userServiceImpl.findById(userId);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found", inventories);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(userId, "INVENTORY", "create")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create", null);
						logger.info("************************ createInventories ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {
					if (inventory.getName() == null || inventory.getName().equals("")
							|| inventory.getActivity() == null || inventory.getActivity().equals("")
							|| inventory.getInventoryNumber() == null || inventory.getInventoryNumber().equals("")
							|| inventory.getStoringCategory() == null || inventory.getStoringCategory().equals("")
					) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,InventoryNumber ,StoringCategory] are Required", null);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}

					User parent = null;

					if (inventory.getId() == null || inventory.getId() == 0) {
						if (user.getAccountType().equals(4)) {
							Set<User> parentClients = user.getUsersOfUser();
							if (parentClients.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are account type 4 and not has parent", null);
								return ResponseEntity.badRequest().body(getObjectResponse);
							} else {
								for (User object : parentClients) {
									parent = object;
								}
								inventory.setUserId(parent.getId());


							}
						} else {
							inventory.setUserId(userId);

						}

						List<Inventory> res = inventoryRepository.checkDublicateAdd(inventory.getUserId(), inventory.getName(), inventory.getInventoryNumber());
						List<Integer> duplictionList = new ArrayList<Integer>();
						List<Inventory> res2 = inventoryRepository.checkDublicateAddByInv(inventory.getInventoryNumber());

						if (!res.isEmpty() || !res2.isEmpty()) {
							for (int i = 0; i < res.size(); i++) {

								if (res.get(i).getName() != null) {
									if (res.get(i).getName().equals(inventory.getName())) {
										duplictionList.add(1);

									}
								}


							}
							for (int i = 0; i < res2.size(); i++) {

								if (res2.get(i).getInventoryNumber() != null) {

									if (res2.get(i).getInventoryNumber().equals(inventory.getInventoryNumber())) {
										duplictionList.add(2);

									}

								}

							}
							getObjectResponse = new GetObjectResponse(201, "This inventorie was found before", duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);

						}


						inventoryRepository.save(inventory);
						inventories.add(inventory);

						if (user.getAccountType().equals(4)) {
							userClientInventory saveData = new userClientInventory();

							Long invId = inventoryRepository.getInventoryIdByName(parent.getId(), inventory.getName(), inventory.getInventoryNumber());
							if (invId != null) {
								saveData.setUserid(userId);
								saveData.setInventoryid(invId);
								userClientInventoryRepository.save(saveData);
							}

						}

						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "sucsess", inventories);
						logger.info("************************ createInventories ENDED ***************************");

						return ResponseEntity.ok().body(getObjectResponse);

					} else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update inventories Id", inventories);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}


				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found", inventories);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}


		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);


		}
	}

	@Override
	public ResponseEntity<?> editInventoriesApp(String TOKEN, Inventory inventory, Long userId) {
		List<Inventory> inventories = new ArrayList<Inventory>();

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "INVENTORY", "edit")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit Inventories", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}

		if (inventory.getId() == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Inventory ID is Required", inventories);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		Inventory inventoryCheck = inventoryRepository.findOne(inventory.getId());
		if (inventoryCheck == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Inventories is not found", inventories);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (inventoryCheck.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Inventories is not found or deleted", inventories);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		inventory.setUserId(inventoryCheck.getUserId());
		Long createdBy = inventory.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}

		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventories.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId, inventory.getId());
			if (inventoryData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}

		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventories", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (inventory.getName() == null || inventory.getName().equals("")
				|| inventory.getActivity() == null || inventory.getActivity().equals("")
				|| inventory.getInventoryNumber() == null || inventory.getInventoryNumber().equals("")
				|| inventory.getStoringCategory() == null || inventory.getStoringCategory().equals("")
		) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,InventoryNumber ,StoringCategory] are Required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);

		} else {


			List<Inventory> res = inventoryRepository.checkDublicateEdit(inventory.getId(), inventory.getUserId(), inventory.getName(), inventory.getInventoryNumber());
			List<Integer> duplictionList = new ArrayList<Integer>();
			List<Inventory> res2 = inventoryRepository.checkDublicateEditByInv(inventory.getId(), inventory.getInventoryNumber());

			if (!res.isEmpty() || !res2.isEmpty()) {
				for (int i = 0; i < res.size(); i++) {

					if (res.get(i).getName() != null) {

						if (res.get(i).getName().equals(inventory.getName())) {
							duplictionList.add(1);

						}
					}


				}
				for (int i = 0; i < res2.size(); i++) {
					if (res2.get(i).getInventoryNumber() != null) {

						if (res2.get(i).getInventoryNumber().equals(inventory.getInventoryNumber())) {
							duplictionList.add(2);

						}
					}


				}
				getObjectResponse = new GetObjectResponse(201, "This inventory was found before", duplictionList);
				return ResponseEntity.ok().body(getObjectResponse);

			}


			inventoryRepository.save(inventory);


			inventories.add(inventory);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Updated Successfully", inventories);
			logger.info("************************ editInventories ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);


		}


	}

	@Override
	public ResponseEntity<?> getInventoryByIdApp(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to return", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		if (userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory = inventoryRepository.findOne(InventoryId);
		if (inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy = inventory.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}
		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId, inventory.getId());
			if (inventoryData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}


		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		List<Inventory> inventories = new ArrayList<Inventory>();
		inventories.add(inventory);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", inventories);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> activeInventoryApp(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "INVENTORY", "delete")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete inventory", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to delete", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory = inventoryRepository.findOne(InventoryId);
		if (inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy = inventory.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}
		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId, inventory.getId());
			if (inventoryData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}

		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		inventory.setDeleteDate(null);
		inventoryRepository.save(inventory);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", null);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> deleteInventoryApp(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "INVENTORY", "delete")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete inventory", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to delete", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory = inventoryRepository.findOne(InventoryId);
		if (inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if (inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy = inventory.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}
		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId, inventory.getId());
			if (inventoryData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}

		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
		inventory.setDeleteDate(date);
		inventory.setWarehouseId(null);
		inventoryRepository.save(inventory);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", null);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> removeWarehouseFromInventoryApp(String TOKEN, Long userId, Long InventoryId,
															 Long warehouseId) {
		// TODO Auto-generated method stub
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "INVENTORY", "assignWarehouse")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to remove Warehouse", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to remove", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory = inventoryRepository.findOne(InventoryId);
		if (inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if (inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Boolean isParentInventory = true;

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId, inventory.getId());
			if (inventoryData.isEmpty()) {
				isParentInventory = false;
			} else {
				isParentInventory = true;
			}


		}

		if (isParentInventory == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		inventory.setWarehouseId(null);
		inventoryRepository.save(inventory);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "removed successfully", null);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> assignWarehouseToInventoryApp(String TOKEN, Long userId, Long InventoryId, Long warehouseId) {
		// TODO Auto-generated method stub
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No userId  to get his inventory or warehouse", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "INVENTORY", "assignWarehouse")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assign Warehouse", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId to assign", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory = inventoryRepository.findOne(InventoryId);
		if (inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if (inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if (warehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No warehouseId  to assign", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse = warehousesRepository.findOne(warehouseId);
		if (warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouseId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if (warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		Boolean isParentWarehouse = true;
		Boolean isParentInventory = true;

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId, inventory.getId());
			if (inventoryData.isEmpty()) {
				isParentInventory = false;
			} else {
				isParentInventory = true;
			}

			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId, warehouse.getId());
			if (warehousesData.isEmpty()) {
				isParentWarehouse = false;
			} else {
				isParentWarehouse = true;
			}
		}

		if (isParentInventory == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (isParentWarehouse == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		inventory.setWarehouseId(warehouseId);
		inventoryRepository.save(inventory);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "assigned successfully", null);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getSelectListInventoriesApp(String TOKEN, Long id) {
		logger.info("************************ getInventoriesList STARTED ***************************");

		List<Inventory> inventories = new ArrayList<Inventory>();

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (id != 0) {

			User user = userServiceImpl.findById(id);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", inventories);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(id, "INVENTORY", "list")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get INVENTORY list", null);
						logger.info("************************ getInventoryList ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {
					List<Long> usersIds = new ArrayList<>();

					userServiceImpl.resetChildernArray();
					if (user.getAccountType().equals(4)) {
						List<Long> inventoryIds = userClientInventoryRepository.getInventoryIds(id);
						if (inventoryIds.size() > 0) {
							inventories = inventoryRepository.getAllInventoriesSelectByIds(inventoryIds);
						}
					} else {
						List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
						if (childernUsers.isEmpty()) {
							usersIds.add(id);
						} else {
							usersIds.add(id);
							for (User object : childernUsers) {
								usersIds.add(object.getId());
							}
						}
						inventories = inventoryRepository.getAllInventoriesSelect(usersIds);
					}


					List<Map> data = new ArrayList<>();
					if (inventories.size() > 0) {

						for (Inventory inventory : inventories) {
							Map InventoriesList = new HashMap();


							InventoriesList.put("id", inventory.getId());
							InventoriesList.put("name", inventory.getName());

							data.add(InventoriesList);

						}


					}
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", data);
					logger.info("************************ getInventoriesList ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", inventories);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getSelectedAndListWarehouseApp(String TOKEN, Long loggedUserId, Long userId,
															Long inventoryId) {
		logger.info("************************ getUnassignedDrivers STARETED ***************************");
		List<Warehouse> war = new ArrayList<>();

		if (TOKEN.equals("")) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", war);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		List<Map> data = new ArrayList<>();
		Map obj = new HashMap();


		if (inventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to delete", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory = inventoryRepository.findOne(inventoryId);
		if (inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if (inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		List<Warehouse> invWarehouse = new ArrayList<Warehouse>();

		if (inventory.getWarehouseId() != null) {
			Warehouse warehouse = warehousesRepository.findOne(inventory.getWarehouseId());
			invWarehouse.add(warehouse);
		}
		obj.put("selectedWarehouses", invWarehouse);

		List<DriverSelect> warehouses = new ArrayList<DriverSelect>();
		if (loggedUserId != 0) {
			User loggedUser = userServiceImpl.findById(loggedUserId);

			if (loggedUser != null) {
				if (loggedUser.getDeleteDate() == null) {
					if (loggedUser.getAccountType().equals(4)) {
						List<Long> usersIds = new ArrayList<>();
						usersIds.add(loggedUserId);
						warehouses = warehousesRepository.getWarehousesSelectClient(usersIds);
						obj.put("warehouses", warehouses);
						data.add(obj);

						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data);
						logger.info("************************ getUnassignedDrivers ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);


					}
				}
			}

		}

		if (userId.equals(0)) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", warehouses);

			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		} else {
			User user = userServiceImpl.findById(userId);

			if (user == null) {

				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found", warehouses);
				return ResponseEntity.status(404).body(getObjectResponse);
			} else {
				if (user.getAccountType().equals(4)) {


					List<Long> usersIds = new ArrayList<>();
					usersIds.add(userId);
					warehouses = warehousesRepository.getWarehousesSelectClient(usersIds);
					obj.put("warehouses", warehouses);
					data.add(obj);
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data);
					logger.info("************************ getUnassignedDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				}

				List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
				List<Long> usersIds = new ArrayList<>();
				if (childernUsers.isEmpty()) {
					usersIds.add(userId);
				} else {
					usersIds.add(userId);
					for (User object : childernUsers) {
						usersIds.add(object.getId());
					}
				}

				warehouses = warehousesRepository.getWarehousesSelect(usersIds);
				obj.put("warehouses", warehouses);
				data.add(obj);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data);
				logger.info("************************ getUnassignedDrivers ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}


		}
	}


	@Override
	public ResponseEntity<?> getWarehousesListApp(String TOKEN, Long id, int offset, String search, int active, String exportData) {
		logger.info("************************ getWarehousesList STARTED ***************************");

		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		Integer size = 0;
		List<Map> data = new ArrayList<>();

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (id != 0) {

			User user = userServiceImpl.findById(id);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", warehouses);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(id, "WAREHOUSE", "list")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get WAREHOUSE list", null);
						logger.info("************************ getWarehouseList ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {
					List<Long> usersIds = new ArrayList<>();
					if (user.getAccountType().equals(4)) {
						List<Long> warehouseIds = userClientWarehouseRepository.getWarhouseIds(id);
						if (warehouseIds.size() > 0) {


							if (active == 0) {
								if (exportData.equals("exportData")) {
									warehouses = warehousesRepository.getWarehousesByIdsDeactiveExport(warehouseIds, search);

								} else {
									warehouses = warehousesRepository.getWarehousesByIdsDeactive(warehouseIds, offset, search);
									size = warehousesRepository.getWarehouseSizeByIdsDeactive(warehouseIds);
								}

							}

							if (active == 2) {
								if (exportData.equals("exportData")) {
									warehouses = warehousesRepository.getWarehousesByIdsAllExport(warehouseIds, search);

								} else {
									warehouses = warehousesRepository.getWarehousesByIdsAll(warehouseIds, offset, search);
									size = warehousesRepository.getWarehouseSizeByIdsAll(warehouseIds);
								}


							}

							if (active == 1) {
								if (exportData.equals("exportData")) {
									warehouses = warehousesRepository.getWarehousesByIdsExport(warehouseIds, search);

								} else {
									warehouses = warehousesRepository.getWarehousesByIds(warehouseIds, offset, search);
									size = warehousesRepository.getWarehouseSizeByIds(warehouseIds);
								}


							}

						}
					} else {
						List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
						if (childernUsers.isEmpty()) {
							usersIds.add(id);
						} else {
							usersIds.add(id);
							for (User object : childernUsers) {
								usersIds.add(object.getId());
							}
						}

						if (active == 0) {
							if (exportData.equals("exportData")) {
								warehouses = warehousesRepository.getWarehousesDeactiveExport(usersIds, search);

							} else {
								warehouses = warehousesRepository.getWarehousesDeactive(usersIds, offset, search);
								size = warehousesRepository.getWarehouseSizeDeactive(usersIds);
							}


						}

						if (active == 2) {
							if (exportData.equals("exportData")) {
								warehouses = warehousesRepository.getWarehousesAllExport(usersIds, search);

							} else {
								warehouses = warehousesRepository.getWarehousesAll(usersIds, offset, search);
								size = warehousesRepository.getWarehouseSizeAll(usersIds);
							}


						}

						if (active == 1) {
							if (exportData.equals("exportData")) {
								warehouses = warehousesRepository.getWarehousesExport(usersIds, search);

							} else {
								warehouses = warehousesRepository.getWarehouses(usersIds, offset, search);
								size = warehousesRepository.getWarehouseSize(usersIds);
							}

						}

					}


					for (Warehouse warehouse : warehouses) {
						Map WarehousesList = new HashMap();

						WarehousesList.put("id", warehouse.getId());
						WarehousesList.put("name", warehouse.getName());
						WarehousesList.put("city", warehouse.getCity());
						WarehousesList.put("phone", warehouse.getPhone());
						WarehousesList.put("email", warehouse.getEmail());
						WarehousesList.put("delete_date", warehouse.getDeleteDate());
						WarehousesList.put("userId", warehouse.getUserId());
						WarehousesList.put("referenceKey", warehouse.getReferenceKey());

						WarehousesList.put("create_date", warehouse.getCreate_date());
						WarehousesList.put("regestration_to_elm_date", warehouse.getRegestration_to_elm_date());
						WarehousesList.put("delete_from_elm_date", warehouse.getDelete_from_elm_date());
						WarehousesList.put("update_date_in_elm", warehouse.getUpdate_date_in_elm());


						WarehousesList.put("userName", null);

						User us = userRepository.findOne(warehouse.getUserId());
						if (us != null) {
							WarehousesList.put("userName", us.getName());

						}
						data.add(WarehousesList);

					}


					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", data, size);
					logger.info("************************ getWarehousesList ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", warehouses);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> createWarehousesApp(String TOKEN, Warehouse warehouse, Long userId) {
		logger.info("************************ createWarehouses STARTED ***************************");
		Date now = new Date();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = isoFormat.format(now);

		warehouse.setCreate_date(nowTime);
		warehouse.setDeleteDate(null);

		String image = warehouse.getPhoto();
		warehouse.setPhoto("not_available.png");

		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (userId != 0) {
			User user = userServiceImpl.findById(userId);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found", warehouses);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "create")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create", null);
						logger.info("************************ createWarehouse ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {
					if (warehouse.getName() == null || warehouse.getName().equals("")
							|| warehouse.getActivity() == null || warehouse.getActivity().equals("")
							|| warehouse.getCity() == null || warehouse.getCity().equals("")
							|| warehouse.getAddress() == null || warehouse.getAddress().equals("")
							|| warehouse.getLatitude() == null || warehouse.getLatitude().equals("")
							|| warehouse.getLongitude() == null || warehouse.getLongitude().equals("")
							|| warehouse.getLandCoordinates() == null || warehouse.getLandCoordinates().equals("")
							|| warehouse.getLicenseNumber() == null || warehouse.getLicenseNumber().equals("")
							|| warehouse.getLicenseIssueDate() == null || warehouse.getLicenseIssueDate().equals("")
							|| warehouse.getLicenseExpiryDate() == null || warehouse.getLicenseExpiryDate().equals("")
							|| warehouse.getPhone() == null || warehouse.getPhone().equals("")
					) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,city ,address , lat ,long ,landCoordinates , licenseNumber ,LicenseIssueDate ,phone and LicenseExpiryDate] are Required", null);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}

					User parent = null;

					if (warehouse.getId() == null || warehouse.getId() == 0) {
						if (user.getAccountType().equals(4)) {
							Set<User> parentClients = user.getUsersOfUser();
							if (parentClients.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are account type 4 and not has parent", null);
								return ResponseEntity.badRequest().body(getObjectResponse);
							} else {
								for (User object : parentClients) {
									parent = object;
								}
								warehouse.setUserId(parent.getId());


							}
						} else {
							warehouse.setUserId(userId);

						}

						List<Warehouse> res = warehousesRepository.checkDublicateAdd(warehouse.getUserId(), warehouse.getEmail(), warehouse.getName(), warehouse.getPhone(), warehouse.getLicenseNumber());
						List<Integer> duplictionList = new ArrayList<Integer>();

						if (!res.isEmpty()) {
							for (int i = 0; i < res.size(); i++) {
								if (res.get(i).getName() != null) {
									if (res.get(i).getName().equals(warehouse.getName())) {
										duplictionList.add(1);

									}
								}
								if (res.get(i).getPhone() != null) {
									if (res.get(i).getPhone().equals(warehouse.getPhone())) {
										duplictionList.add(2);

									}
								}

								if (res.get(i).getLicenseNumber() != null) {
									if (res.get(i).getLicenseNumber().equals(warehouse.getLicenseNumber())) {
										duplictionList.add(3);

									}
								}

								if (res.get(i).getEmail() != null) {
									if (res.get(i).getEmail().equals(warehouse.getEmail())) {
										duplictionList.add(4);

									}
								}


							}
							getObjectResponse = new GetObjectResponse(201, "This warehouse was found before", duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);

						}


						DecodePhotoSFDA decodePhoto = new DecodePhotoSFDA();
						if (image != null) {
							if (image != "") {
								if (image.startsWith("data:image")) {
									warehouse.setPhoto(decodePhoto.Base64_Image(image, "warehouse"));

								}
							}
						}

						warehousesRepository.save(warehouse);
						warehouses.add(warehouse);

						if (user.getAccountType().equals(4)) {
							userClientWarehouse saveData = new userClientWarehouse();

							Long wareId = warehousesRepository.getWarehouseIdByName(parent.getId(), warehouse.getName(), warehouse.getLicenseNumber());
							if (wareId != null) {
								saveData.setUserid(userId);
								saveData.setWarehouseid(wareId);
								userClientWarehouseRepository.save(saveData);
							}

						}

						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "sucsess", warehouses);
						logger.info("************************ createWarehouses ENDED ***************************");

						return ResponseEntity.ok().body(getObjectResponse);

					} else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update warehouse Id", warehouses);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}


				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found", warehouses);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}


		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);


		}
	}

	@Override
	public ResponseEntity<?> editWarehousesApp(String TOKEN, Warehouse warehouse, Long userId) {

		// TODO Auto-generated method stub
		List<Warehouse> warehouses = new ArrayList<Warehouse>();

		String newPhoto = warehouse.getPhoto();
		warehouse.setPhoto("not_available.png");

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "edit")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit warehouses", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}

		if (warehouse.getId() == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "warehouses ID is Required", warehouses);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		Warehouse warehouseCheck = warehousesRepository.findOne(warehouse.getId());
		if (warehouseCheck == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "warehouses is not found", warehouses);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (warehouseCheck.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "warehouses is not found or deleted", warehouses);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		warehouse.setUserId(warehouseCheck.getUserId());
		Long createdBy = warehouse.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}

		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouses.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId, warehouse.getId());
			if (warehousesData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}


		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouses", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (warehouse.getName() == null || warehouse.getName().equals("")
				|| warehouse.getActivity() == null || warehouse.getActivity().equals("")
				|| warehouse.getCity() == null || warehouse.getCity().equals("")
				|| warehouse.getAddress() == null || warehouse.getAddress().equals("")
				|| warehouse.getLatitude() == null || warehouse.getLatitude().equals("")
				|| warehouse.getLongitude() == null || warehouse.getLongitude().equals("")
				|| warehouse.getLandCoordinates() == null || warehouse.getLandCoordinates().equals("")
				|| warehouse.getLicenseNumber() == null || warehouse.getLicenseNumber().equals("")
				|| warehouse.getLicenseIssueDate() == null || warehouse.getLicenseIssueDate().equals("")
				|| warehouse.getLicenseExpiryDate() == null || warehouse.getLicenseExpiryDate().equals("")
				|| warehouse.getPhone() == null || warehouse.getPhone().equals("")
		) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,city ,address , lat ,long ,landCoordinates , licenseNumber ,LicenseIssueDate ,phone and LicenseExpiryDate] are Required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		} else {


			List<Warehouse> res = warehousesRepository.checkDublicateEdit(warehouse.getId(), warehouse.getUserId(), warehouse.getEmail(), warehouse.getName(), warehouse.getPhone(), warehouse.getLicenseNumber());
			List<Integer> duplictionList = new ArrayList<Integer>();

			if (!res.isEmpty()) {
				for (int i = 0; i < res.size(); i++) {
					if (res.get(i).getName() != null) {
						if (res.get(i).getName().equals(warehouse.getName())) {
							duplictionList.add(1);

						}
					}

					if (res.get(i).getPhone() != null) {
						if (res.get(i).getPhone().equals(warehouse.getPhone())) {
							duplictionList.add(2);

						}
					}

					if (res.get(i).getLicenseNumber() != null) {
						if (res.get(i).getLicenseNumber().equals(warehouse.getLicenseNumber())) {
							duplictionList.add(3);

						}
					}

					if (res.get(i).getEmail() != null) {
						if (res.get(i).getEmail().equals(warehouse.getEmail())) {
							duplictionList.add(4);

						}
					}


				}
				getObjectResponse = new GetObjectResponse(201, "This warehouse was found before", duplictionList);
				return ResponseEntity.ok().body(getObjectResponse);

			}


			DecodePhotoSFDA decodePhoto = new DecodePhotoSFDA();
			String oldPhoto = warehouseCheck.getPhoto();

			if (oldPhoto != null) {
				if (!oldPhoto.equals("")) {
					if (!oldPhoto.equals("not_available.png")) {
						decodePhoto.deletePhoto(oldPhoto, "warehouse");
					}
				}
			}


			if (newPhoto == null) {
				warehouse.setPhoto("not_available.png");
			} else {
				if (newPhoto.equals("")) {

					warehouse.setPhoto("not_available.png");
				} else {
					if (newPhoto.equals(oldPhoto)) {
						warehouse.setPhoto(oldPhoto);
					} else {
						if (newPhoto.startsWith("data:image")) {

							warehouse.setPhoto(decodePhoto.Base64_Image(newPhoto, "warehouse"));
						}
					}

				}
			}

			warehousesRepository.save(warehouse);


			warehouses.add(warehouse);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Updated Successfully", warehouses);
			logger.info("************************ editWarehouse ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

		}

	}

	@Override
	public ResponseEntity<?> getWarehouseByIdApp(String TOKEN, Long WarehouseId, Long userId) {
		// TODO Auto-generated method stub

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to return", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		if (userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse = warehousesRepository.findOne(WarehouseId);
		if (warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy = warehouse.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}
		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}


		if (loggedUser.getAccountType().equals(4)) {
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId, warehouse.getId());
			if (warehousesData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}

		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		warehouses.add(warehouse);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", warehouses);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> deleteWarehouseApp(String TOKEN, Long WarehouseId, Long userId) {
		// TODO Auto-generated method stub
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "delete")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete warehouse", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to delete", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse = warehousesRepository.findOne(WarehouseId);
		if (warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this WarehouseId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if (warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Warehouse not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy = warehouse.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}
		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId, warehouse.getId());
			if (warehousesData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}


		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
		warehouse.setDeleteDate(date);

		warehousesRepository.save(warehouse);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", null);
		return ResponseEntity.ok().body(getObjectResponse);


	}

	@Override
	public ResponseEntity<?> getListSelectWarehouseApp(String TOKEN, Long userId) {

		logger.info("************************ getWarehousesList STARTED ***************************");
		List<Map> data = new ArrayList<>();

		List<Warehouse> warehouses = new ArrayList<Warehouse>();

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (userId != 0) {

			User user = userServiceImpl.findById(userId);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", warehouses);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "list")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get WAREHOUSE list", null);
						logger.info("************************ getWarehouseList ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {
					List<Long> usersIds = new ArrayList<>();

					userServiceImpl.resetChildernArray();
					if (user.getAccountType().equals(4)) {


						List<Long> warehouseIds = userClientWarehouseRepository.getWarhouseIds(userId);
						if (warehouseIds.size() > 0) {
							warehouses = warehousesRepository.getAllWarehousesSelectByIds(warehouseIds);

							if (warehouses.size() > 0) {

								for (Warehouse warehouse : warehouses) {
									Map WarehousesList = new HashMap();


									WarehousesList.put("id", warehouse.getId());
									WarehousesList.put("name", warehouse.getName());

									data.add(WarehousesList);

								}


							}

						}

					} else {
						List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
						if (childernUsers.isEmpty()) {
							usersIds.add(userId);
						} else {
							usersIds.add(userId);
							for (User object : childernUsers) {
								usersIds.add(object.getId());
							}
						}
						warehouses = warehousesRepository.getAllWarehousesSelect(usersIds);
						if (warehouses.size() > 0) {

							for (Warehouse warehouse : warehouses) {
								Map WarehousesList = new HashMap();


								WarehousesList.put("id", warehouse.getId());
								WarehousesList.put("name", warehouse.getName());

								data.add(WarehousesList);

							}


						}
					}


					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", data);
					logger.info("************************ getWarehousesList ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", warehouses);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getListWarehouseMapApp(String TOKEN, Long userId) {
		logger.info("************************ getWarehousesList STARTED ***************************");
		List<Warehouse> warehouses = new ArrayList<Warehouse>();

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (userId != 0) {

			User user = userServiceImpl.findById(userId);
			if (user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", warehouses);
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {
				if (user.getAccountType() != 1) {
					if (!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "list")) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get WAREHOUSE list", null);
						logger.info("************************ getWarehouseList ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if (user.getDeleteDate() == null) {
					List<Long> usersIds = new ArrayList<>();

					userServiceImpl.resetChildernArray();
					if (user.getAccountType().equals(4)) {
						List<Long> warehouseIds = userClientWarehouseRepository.getWarhouseIds(userId);
						if (warehouseIds.size() > 0) {
							warehouses = warehousesRepository.getAllWarehousesSelectByIds(warehouseIds);

						}

					} else {
						List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
						if (childernUsers.isEmpty()) {
							usersIds.add(userId);
						} else {
							usersIds.add(userId);
							for (User object : childernUsers) {
								usersIds.add(object.getId());
							}
						}
						warehouses = warehousesRepository.getAllWarehousesSelect(usersIds);

					}


					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success", warehouses);
					logger.info("************************ getWarehousesList ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				} else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found", warehouses);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}

		} else {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> activeWarehouseApp(String TOKEN, Long WarehouseId, Long userId) {
		// TODO Auto-generated method stub
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (loggedUser.getAccountType() != 1) {
			if (!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "delete")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete warehouse", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if (WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to delete", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse = warehousesRepository.findOne(WarehouseId);
		if (warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this WarehouseId not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy = warehouse.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}
		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId, warehouse.getId());
			if (warehousesData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}

		if (loggedUser.getAccountType().equals(4)) {
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId, warehouse.getId());
			if (warehousesData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}


		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		warehouse.setDeleteDate(null);

		warehousesRepository.save(warehouse);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", null);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getInventoryListOfWarehouseMapApp(String TOKEN, Long userId, Long WarehouseId) {
		// TODO Auto-generated method stub

		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		if (WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to return", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		if (userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse = warehousesRepository.findOne(WarehouseId);
		if (warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if (warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found or deleted", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		User loggedUser = userServiceImpl.findById(userId);
		if (loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy = warehouse.getUserId();
		Boolean isParent = false;

		if (createdBy.toString().equals(userId.toString())) {
			isParent = true;
		}
		List<User> childs = new ArrayList<User>();
		if (loggedUser.getAccountType().equals(4)) {
			List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
			if (parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.", null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			} else {
				User parentClient = new User();

				for (User object : parents) {
					parentClient = object;
					break;
				}

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			}

		} else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}


		User parentChilds = new User();
		if (!childs.isEmpty()) {
			for (User object : childs) {
				parentChilds = object;
				if (parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent = true;
					break;
				}
			}
		}


		if (loggedUser.getAccountType().equals(4)) {
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId, warehouse.getId());
			if (warehousesData.isEmpty()) {
				isParent = false;
			} else {
				isParent = true;
			}
		}

		if (isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		List<Inventory> inventories = new ArrayList<Inventory>();

		inventories = inventoryRepository.getAllInventoriesOfWarehouseList(WarehouseId);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", inventories);
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getInventoriesNotificationsApp(String TOKEN, Long userId, int offset, String search) {
		logger.info("************************ getNotifications STARTED ***************************");

		List<InventoryNotification> notifications = new ArrayList<InventoryNotification>();
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", notifications);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		List<Long> allInventories = new ArrayList<Long>();
		List<Long> usersIds = new ArrayList<>();
		if (userId != 0) {

			User user = userServiceImpl.findById(userId);
			if (user != null) {
				userServiceImpl.resetChildernArray();

				if (user.getAccountType() == 4) {
					allInventories = userClientInventoryRepository.getInventoryIds(userId);

				} else {
					usersIds.add(userId);
					allInventories = inventoryRepository.getAllInventoriesIds(usersIds);

				}


				notifications = mongoInventoryNotificationRepo.getNotificationsToday(allInventories, offset);
				Integer size = 0;
				if (notifications.size() > 0) {
					//size= mongoInventoryNotificationRepo.getNotificationsTodaySize(allInventories);
					for (int i = 0; i < notifications.size(); i++) {

						Inventory inventory = inventoryRepository.findOne(notifications.get(i).getInventory_id());
						if (inventory != null) {
							notifications.get(i).setInventoryName(inventory.getName());

						}
					}

				}


				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", notifications, size);
				logger.info("************************ getNotifications ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);


			} else {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found", notifications);
				logger.info("************************ getNotifications ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);

			}


		} else {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", notifications);
			logger.info("************************ getNotifications ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);

		}

	}

	@Override
	public ResponseEntity<?> getAllInventoriesLastInfoApp(String TOKEN, Long userId, int offset, String search) {
		logger.info("************************ getAllInventoriesLastInfo STARTED ***************************");

		List<InventoryLastData> inventories = new ArrayList<InventoryLastData>();
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}
		List<Long> allInventories = new ArrayList<Long>();
		List<Long> usersIds = new ArrayList<>();
		if (userId != 0) {

			User user = userServiceImpl.findById(userId);
			if (user != null) {
				userServiceImpl.resetChildernArray();

				if (user.getAccountType() == 4) {

					allInventories = userClientInventoryRepository.getInventoryIds(userId);

				} else {
					usersIds.add(userId);

					List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
					if (childernUsers.isEmpty()) {
						usersIds.add(userId);
					} else {
						usersIds.add(userId);
						for (User object : childernUsers) {
							usersIds.add(object.getId());
						}
					}

					allInventories = inventoryRepository.getAllInventoriesIds(usersIds);

				}


				inventories = mongoInventoryLastDataRepo.getLastData(allInventories, offset);
				Integer size = 0;
				if (inventories.size() > 0) {
					size = mongoInventoryLastDataRepo.getLastDataSize(allInventories);
					for (int i = 0; i < inventories.size(); i++) {

						Inventory inventory = inventoryRepository.findOne(inventories.get(i).getInventory_id());
						if (inventory != null) {
							inventories.get(i).setInventoryName(inventory.getName());

						}
					}

				}


				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", inventories, size);
				logger.info("************************ getNotifications ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);


			} else {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found", inventories);
				logger.info("************************ getNotifications ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);

			}


		} else {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", inventories);
			logger.info("************************ getNotifications ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getInventoryStatusApp(String TOKEN, Long userId) {
		logger.info("************************ getDevicesStatusAndDrives STARTED ***************************");
		if (TOKEN.equals("")) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", devices);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (super.checkActive(TOKEN) != null) {
			return super.checkActive(TOKEN);
		}


		if (userId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required", devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (userId == 0) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required", null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if (user == null) {

			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found", null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		List<Long> usersIds = new ArrayList<>();

		Integer inventoryON = 0;
		Integer inventoryOFF = 0;
		Integer inventoryOutOfNetwork = 0;
		Integer totalInventories = 0;
		Integer inventoryNoData = 0;

		Integer totalWarehouses = 0;

		if (user.getAccountType().equals(4)) {
			List<Long> inventoryIds = userClientInventoryRepository.getInventoryIds(userId);
			List<Long> warehouseIds = userClientWarehouseRepository.getWarehouseIds(userId);

			if (warehouseIds.size() > 0) {
				totalWarehouses = warehousesRepository.getTotalNumberOfUserWarehouseByIds(warehouseIds);

			}

			if (inventoryIds.size() > 0) {
				List<String> onlineInventoryIds = inventoryRepository.getNumberOfOnlineInventoryListByIds(inventoryIds);
				inventoryON = onlineInventoryIds.size();

				List<String> offlineInventoryIds = inventoryRepository.getNumberOfOfflineInventoryListByIds(inventoryIds);
				inventoryOFF = offlineInventoryIds.size();

				List<String> outInventoryIds = inventoryRepository.getNumberOfOutOfNetworkInventoryListByIds(inventoryIds);
				inventoryOutOfNetwork = outInventoryIds.size();

				totalInventories = inventoryRepository.getTotalNumberOfUserInventoryByIds(inventoryIds);
				inventoryNoData = inventoryRepository.getTotalNumberOfUserInventoryNoDataByIds(inventoryIds);
			}
		} else {
			List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
			if (childernUsers.isEmpty()) {
				usersIds.add(userId);
			} else {
				usersIds.add(userId);
				for (User object : childernUsers) {
					usersIds.add(object.getId());
				}
			}
			List<String> onlineInventoryIds = inventoryRepository.getNumberOfOnlineInventoryList(usersIds);
			inventoryON = onlineInventoryIds.size();

			List<String> offlineInventoryIds = inventoryRepository.getNumberOfOfflineInventoryList(usersIds);
			inventoryOFF = offlineInventoryIds.size();

			List<String> outInventoryIds = inventoryRepository.getNumberOfOutOfNetworkInventoryList(usersIds);
			inventoryOutOfNetwork = outInventoryIds.size();

			totalInventories = inventoryRepository.getTotalNumberOfUserInventory(usersIds);
			inventoryNoData = inventoryRepository.getTotalNumberOfUserInventoryNoData(usersIds);

			totalWarehouses = warehousesRepository.getTotalNumberOfUserWarehouse(usersIds);


		}


		Map invStatus = new HashMap();

		invStatus.put("inventoryON", inventoryON);
		invStatus.put("inventoryOFF", inventoryOFF);
		invStatus.put("inventoryOutOfNetwork", inventoryOutOfNetwork);
		invStatus.put("totalInventories", totalInventories);
		invStatus.put("inventoryNoData", inventoryNoData);
		invStatus.put("warehouses", totalWarehouses);

		List<Map> data = new ArrayList<>();
		data.add(invStatus);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data);
		logger.info("************************ invStatus ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getInventoriesReportApp(String TOKEN, Long[] inventoryIds, int offset, String start,
													 String end, String search, Long userId, String exportData) {
		logger.info("************************ getInventoriesReport STARTED ***************************");
		List<InventoryLastData> inventoriesReport = new ArrayList<InventoryLastData>();
		if (TOKEN.equals("")) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventoriesReport);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		if (!TOKEN.equals("Schedule")) {
			if (super.checkActive(TOKEN) != null) {
				return super.checkActive(TOKEN);
			}
		}


		User loggedUser = new User();
		if (userId != 0) {

			loggedUser = userServiceImpl.findById(userId);
			if (loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found", inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
		}

		if (!loggedUser.getAccountType().equals(1)) {
			if (!userRoleService.checkUserHasPermission(userId, "INVENTORYTEMPHUMD", "list")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get inventoriesReport list", inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}


		List<Long> allInventories = new ArrayList<>();

		if (inventoryIds.length != 0) {
			for (Long inventoryId : inventoryIds) {
				if (inventoryId != 0) {
					Inventory inventory = inventoryRepository.findOne(inventoryId);
					if (inventory != null) {

						Long createdBy = inventory.getUserId();
						Boolean isParent = false;

						if (createdBy.toString().equals(userId.toString())) {
							isParent = true;
						}
						List<User> childs = new ArrayList<User>();
						if (loggedUser.getAccountType().equals(4)) {
							List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
							if (parents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.", null);
								return ResponseEntity.badRequest().body(getObjectResponse);
							} else {
								User parentClient = new User();

								for (User object : parents) {
									parentClient = object;
									break;
								}

								userServiceImpl.resetChildernArray();
								childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
							}

						} else {
							userServiceImpl.resetChildernArray();
							childs = userServiceImpl.getAllChildernOfUser(userId);
						}


						User parentChilds = new User();
						if (!childs.isEmpty()) {
							for (User object : childs) {
								parentChilds = object;
								if (parentChilds.getId().toString().equals(createdBy.toString())) {
									isParent = true;
									break;
								}
							}
						}
						if (isParent == false) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory", null);
							return ResponseEntity.badRequest().body(getObjectResponse);
						}

						allInventories.add(inventoryId);
					}

				}
			}
		} else {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Inventory is not found", inventoriesReport);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Date dateFrom;
		Date dateTo;
		if (start.equals("0") || end.equals("0")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required", null);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);

		} else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);


			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block

					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getInventoriesReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}

			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getInventoriesReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}


			Date today = new Date();

			if (dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date", null);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			if (today.getTime() < dateFrom.getTime() || today.getTime() < dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today", null);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
		List<InventoryLastData> data = new ArrayList<InventoryLastData>();
		Integer size = 0;


		if (exportData.equals("exportData")) {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);

			if (data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());

					}
				}

			}
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data, size);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}

		if (!TOKEN.equals("Schedule")) {
			data = mongoInventoryLastDataRepo.getInventoriesReport(allInventories, offset, dateFrom, dateTo);

			if (data.size() > 0) {
				size = mongoInventoryLastDataRepo.getInventoriesReportSize(allInventories, dateFrom, dateTo);
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());

					}
				}

			}

		} else {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);
			if (data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());

					}
				}

			}

		}

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data, size);
		logger.info("************************ getInventoriesReport ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getWarehousesReportApp(String TOKEN, Long[] warehouseIds, int offset, String start, String end,
													String search, Long userId, String exportData) {

		logger.info("************************ getWarehousesReport STARTED ***************************");
		List<InventoryLastData> inventoriesReport = new ArrayList<InventoryLastData>();
		if (TOKEN.equals("")) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventoriesReport);
			logger.info("************************ getWarehousesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		if (!TOKEN.equals("Schedule")) {
			if (super.checkActive(TOKEN) != null) {
				return super.checkActive(TOKEN);
			}
		}


		User loggedUser = new User();
		if (userId != 0) {

			loggedUser = userServiceImpl.findById(userId);
			if (loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found", inventoriesReport);
				logger.info("************************ getWarehousesReport ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
		}

		if (!loggedUser.getAccountType().equals(1)) {
			if (!userRoleService.checkUserHasPermission(userId, "WAREHOUSETEMPHUMD", "list")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get inventoriesReport list", inventoriesReport);
				logger.info("************************ getWarehousesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}


		List<Long> allInventories = new ArrayList<>();
		List<Long> allWarehouses = new ArrayList<>();

		if (warehouseIds.length != 0) {
			for (Long warehouseId : warehouseIds) {
				if (warehouseId != 0) {
					Warehouse warehouse = warehousesRepository.findOne(warehouseId);
					if (warehouse != null) {
						Long createdBy = warehouse.getUserId();
						Boolean isParent = false;

						if (createdBy.toString().equals(userId.toString())) {
							isParent = true;
						}

						List<User> childs = new ArrayList<User>();
						if (loggedUser.getAccountType().equals(4)) {
							List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
							if (parents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouses.", null);
								return ResponseEntity.badRequest().body(getObjectResponse);
							} else {
								User parentClient = new User();

								for (User object : parents) {
									parentClient = object;
									break;
								}

								userServiceImpl.resetChildernArray();
								childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
							}

						} else {
							userServiceImpl.resetChildernArray();
							childs = userServiceImpl.getAllChildernOfUser(userId);
						}


						User parentChilds = new User();
						if (!childs.isEmpty()) {
							for (User object : childs) {
								parentChilds = object;
								if (parentChilds.getId().toString().equals(createdBy.toString())) {
									isParent = true;
									break;
								}
							}
						}
						if (isParent == false) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouses", null);
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allWarehouses.add(warehouseId);

					}

				}
			}
		} else {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Warehouse is not found", inventoriesReport);
			logger.info("************************ getWarehousesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if (allWarehouses.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Warehouse is not found", inventoriesReport);
			logger.info("************************ getWarehousesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		} else {
			allInventories = inventoryRepository.getAllInventoriesOfWarehouse(allWarehouses);
			if (allInventories.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No Inventories for those Warehouses", inventoriesReport);
				logger.info("************************ getWarehousesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}


		Date dateFrom;
		Date dateTo;
		if (start.equals("0") || end.equals("0")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required", null);
			logger.info("************************ getWarehousesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);

		} else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);


			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block

					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getWarehousesReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}

			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getWarehousesReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}


			Date today = new Date();

			if (dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date", null);
				logger.info("************************ getWarehousesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			if (today.getTime() < dateFrom.getTime() || today.getTime() < dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today", null);
				logger.info("************************ getWarehousesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
		List<InventoryLastData> data = new ArrayList<InventoryLastData>();
		Integer size = 0;


		if (exportData.equals("exportData")) {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);

			if (data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data, size);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}

		if (!TOKEN.equals("Schedule")) {
			data = mongoInventoryLastDataRepo.getInventoriesReport(allInventories, offset, dateFrom, dateTo);

			if (data.size() > 0) {
				size = mongoInventoryLastDataRepo.getInventoriesReportSize(allInventories, dateFrom, dateTo);
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}

		} else {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);
			if (data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}

		}

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data, size);
		logger.info("************************ getInventoriesReport ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);


	}

	@Override
	public ResponseEntity<?> getNotificationReportApp(String TOKEN, Long[] inventoryIds, Long[] warehouseIds, int offset,
													  String start, String end, String search, Long userId, String exportData) {
		logger.info("************************ getInventoriesReport STARTED ***************************");
		List<InventoryLastData> inventoriesReport = new ArrayList<InventoryLastData>();
		if (TOKEN.equals("")) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", inventoriesReport);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		if (!TOKEN.equals("Schedule")) {
			if (super.checkActive(TOKEN) != null) {
				return super.checkActive(TOKEN);
			}
		}


		User loggedUser = new User();
		if (userId != 0) {

			loggedUser = userServiceImpl.findById(userId);
			if (loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found", inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
		}

		if (!loggedUser.getAccountType().equals(1)) {
			if (!userRoleService.checkUserHasPermission(userId, "NOTIFICATIONTEMPHUMD", "list")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get inventoriesReport list", inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}


		List<Long> allInventories = new ArrayList<>();

		if (inventoryIds.length != 0) {
			for (Long inventoryId : inventoryIds) {
				if (inventoryId != 0) {
					Inventory inventory = inventoryRepository.findOne(inventoryId);
					if (inventory != null) {

						Long createdBy = inventory.getUserId();
						Boolean isParent = false;

						if (createdBy.toString().equals(userId.toString())) {
							isParent = true;
						}
						List<User> childs = new ArrayList<User>();
						if (loggedUser.getAccountType().equals(4)) {
							List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
							if (parents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.", null);
								return ResponseEntity.badRequest().body(getObjectResponse);
							} else {
								User parentClient = new User();

								for (User object : parents) {
									parentClient = object;
									break;
								}

								userServiceImpl.resetChildernArray();
								childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
							}

						} else {
							userServiceImpl.resetChildernArray();
							childs = userServiceImpl.getAllChildernOfUser(userId);
						}


						User parentChilds = new User();
						if (!childs.isEmpty()) {
							for (User object : childs) {
								parentChilds = object;
								if (parentChilds.getId().toString().equals(createdBy.toString())) {
									isParent = true;
									break;
								}
							}
						}
						if (isParent == false) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory", null);
							return ResponseEntity.badRequest().body(getObjectResponse);
						}

						allInventories.add(inventoryId);
					}

				}
			}
		}

		List<Long> allWarehouses = new ArrayList<>();

		if (warehouseIds.length != 0) {
			for (Long warehouseId : warehouseIds) {
				if (warehouseId != 0) {
					Warehouse warehouse = warehousesRepository.findOne(warehouseId);
					if (warehouse != null) {
						Long createdBy = warehouse.getUserId();
						Boolean isParent = false;

						if (createdBy.toString().equals(userId.toString())) {
							isParent = true;
						}

						List<User> childs = new ArrayList<User>();
						if (loggedUser.getAccountType().equals(4)) {
							List<User> parents = userServiceImpl.getAllParentsOfuser(loggedUser, loggedUser.getAccountType());
							if (parents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouses.", null);
								return ResponseEntity.badRequest().body(getObjectResponse);
							} else {
								User parentClient = new User();

								for (User object : parents) {
									parentClient = object;
									break;
								}

								userServiceImpl.resetChildernArray();
								childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
							}

						} else {
							userServiceImpl.resetChildernArray();
							childs = userServiceImpl.getAllChildernOfUser(userId);
						}


						User parentChilds = new User();
						if (!childs.isEmpty()) {
							for (User object : childs) {
								parentChilds = object;
								if (parentChilds.getId().toString().equals(createdBy.toString())) {
									isParent = true;
									break;
								}
							}
						}
						if (isParent == false) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouses", null);
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allWarehouses.add(warehouseId);

					}

				}
			}
		}

		if (!allWarehouses.isEmpty()) {
			allInventories.addAll(inventoryRepository.getAllInventoriesOfWarehouse(allWarehouses));

		}

		Date dateFrom;
		Date dateTo;
		if (start.equals("0") || end.equals("0")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required", null);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);

		} else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);


			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block

					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getInventoriesReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}

			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getInventoriesReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}


			Date today = new Date();

			if (dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date", null);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			if (today.getTime() < dateFrom.getTime() || today.getTime() < dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today", null);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}


		List<InventoryNotification> data = new ArrayList<InventoryNotification>();
		Integer size = 0;


		if (exportData.equals("exportData")) {
			data = mongoInventoryNotificationRepo.getNotificationsReportSchedule(allInventories, dateFrom, dateTo);
			if (data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}

			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data, size);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}

		if (!TOKEN.equals("Schedule")) {
			data = mongoInventoryNotificationRepo.getNotificationsReport(allInventories, offset, dateFrom, dateTo);
			if (data.size() > 0) {
				size = mongoInventoryNotificationRepo.getNotificationsReportSize(allInventories, dateFrom, dateTo);
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}

		} else {
			data = mongoInventoryNotificationRepo.getNotificationsReportSchedule(allInventories, dateFrom, dateTo);
			if (data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if (inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}

		}

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", data, size);
		logger.info("************************ getInventoriesReport ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getVehicleTempHumApp(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
												  String end, String search, Long userId, String exportData) {
		logger.info("************************ getSensorsReport STARTED ***************************");

		List<DeviceTempHum> positionsList = new ArrayList<DeviceTempHum>();
		if (TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required", positionsList);
			logger.info("************************ getSensorsReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}


		if (!TOKEN.equals("Schedule")) {
			if (super.checkActive(TOKEN) != null) {
				return super.checkActive(TOKEN);
			}
		}


		User loggedUser = new User();
		if (userId != 0) {

			loggedUser = userServiceImpl.findById(userId);
			if (loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found", positionsList);
				logger.info("************************ getSensorsReport ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
		}

		if (!loggedUser.getAccountType().equals(1)) {
			if (!userRoleService.checkUserHasPermission(userId, "SENSORWEIGHT", "list")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SENSORWEIGHT list", positionsList);
				logger.info("************************ getSensorsReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}


		List<Long> allDevices = new ArrayList<>();

		if (groupIds.length != 0) {
			for (Long groupId : groupIds) {
				if (groupId != 0) {
					Group group = groupRepository.findOne(groupId);
					if (group != null) {
						if (group.getIs_deleted() == null) {
							boolean isParent = false;
							if (loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if (clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group", positionsList);
									logger.info("************************ getSensorsReport ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								} else {
									User parent = null;
									for (User object : clientParents) {
										parent = object;
									}

									Set<User> groupParents = group.getUserGroup();
									if (groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group", positionsList);
										logger.info("************************ getSensorsReport ENDED ***************************");
										return ResponseEntity.badRequest().body(getObjectResponse);
									} else {
										for (User parentObject : groupParents) {
											if (parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
								List<Long> CheckData = userClientGroupRepository.getGroup(userId, groupId);
								if (CheckData.isEmpty()) {
									isParent = false;
								} else {
									isParent = true;
								}
							}
							if (!groupsServiceImpl.checkIfParent(group, loggedUser) && !isParent) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ", positionsList);
								logger.info("************************ getSensorsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if (group.getType() != null) {
								if (group.getType().equals("driver")) {

									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));


								} else if (group.getType().equals("device")) {

									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));


								} else if (group.getType().equals("geofence")) {

									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));


								}
							}


						}
					}


				}
			}
		}
		if (deviceIds.length != 0) {
			for (Long deviceId : deviceIds) {
				if (deviceId != 0) {
					Device device = deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if (loggedUser.getAccountType() == 4) {
						Set<User> parentClients = loggedUser.getUsersOfUser();
						if (parentClients.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ", positionsList);
							logger.info("************************ getSensorsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						} else {
							User parent = null;
							for (User object : parentClients) {
								parent = object;
							}
							Set<User> deviceParent = device.getUser();
							if (deviceParent.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ", positionsList);
								logger.info("************************ getSensorsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							} else {
								for (User parentObject : deviceParent) {
									if (parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId, deviceId);
						if (CheckData.isEmpty()) {
							isParent = false;
						} else {
							isParent = true;
						}
					}
					if (!deviceServiceImpl.checkIfParent(device, loggedUser) && !isParent) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device", positionsList);
						logger.info("************************ getSensorsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}

					allDevices.add(deviceId);


				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if (start.equals("0") || end.equals("0")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required", null);
			logger.info("************************ getEventsReport ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);

		} else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);


			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block

					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getEventsReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}

			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null);
					logger.info("************************ getEventsReport ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}

			}


			Date today = new Date();

			if (dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date", null);
				logger.info("************************ getEventsReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			if (today.getTime() < dateFrom.getTime() || today.getTime() < dateTo.getTime()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today", null);
				logger.info("************************ getEventsReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

			search = "%" + search + "%";

			String appendString = "";

			if (allDevices.size() > 0) {
				for (int i = 0; i < allDevices.size(); i++) {
					if (appendString != "") {
						appendString += "," + allDevices.get(i);
					} else {
						appendString += allDevices.get(i);
					}
				}
			}
			allDevices = new ArrayList<Long>();

			String[] data = {};
			if (!appendString.equals("")) {
				data = appendString.split(",");

			}


			for (String d : data) {

				if (!allDevices.contains(Long.parseLong(d))) {
					allDevices.add(Long.parseLong(d));
				}
			}

			if (allDevices.isEmpty()) {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ", positionsList);
				logger.info("************************ getSensorsReport ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		Integer size = 0;

		if (exportData.equals("exportData")) {

			positionsList = mongoPositionRepoSFDA.getVehicleTempHumListScheduled(allDevices, dateFrom, dateTo);

			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", positionsList);
			logger.info("************************ getSensorsReport ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

		}
		if (!TOKEN.equals("Schedule")) {
			search = "%" + search + "%";
			positionsList = mongoPositionRepoSFDA.getVehicleTempHumList(allDevices, offset, dateFrom, dateTo);
			if (positionsList.size() > 0) {
				size = mongoPositionRepoSFDA.getVehicleTempHumListSize(allDevices, dateFrom, dateTo);

			}

		} else {
			positionsList = mongoPositionRepoSFDA.getVehicleTempHumListScheduled(allDevices, dateFrom, dateTo);

		}

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", positionsList, size);
		logger.info("************************ getSensorsReport ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ApiResponse<List<WareHouseInvLastDataResponse>> getWareHouseInvLastData(String TOKEN, Long userId,  int whSize, int offset, String search) {

		logger.info("****************** getWareHouseInvLastData STARTED ******************");
		List<WareHouseInvLastDataResponse> results = new ArrayList<>();
		ApiResponseBuilder<List<WareHouseInvLastDataResponse>> builder = new ApiResponseBuilder<>();

		if (TOKEN.equals("")) {
			builder.setMessage("TOKEN id is required");
			builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
			builder.setEntity(results);
			builder.setSize(results.size());
			builder.setSuccess(false);
			return builder.build();
		}

		if (super.checkActiveByApi(TOKEN) != null) {
			return super.checkActiveByApi(TOKEN);
		}
		if (userId != 0) {
			User user = userServiceImpl.findById(userId);
			if (user == null) {
				builder.setMessage("This User is not Found");
				builder.setStatusCode(HttpStatus.NOT_FOUND.value());
				builder.setEntity(results);
				builder.setSize(results.size());
				builder.setSuccess(false);
				return builder.build();
			}
			if (user.getDeleteDate() != null) {
				builder.setMessage("This User Was Delete at : " + user.getDeleteDate());
				builder.setStatusCode(HttpStatus.NOT_FOUND.value());
				builder.setEntity(results);
				builder.setSize(results.size());
				builder.setSuccess(false);
				return builder.build();
			}

			List<Long> userIds = new ArrayList<>();

			userServiceImpl.resetChildernArray();
			if (user.getAccountType().equals(4)) {
				userIds.add(userId);
			} else {
				List<User> childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
				if (childernUsers.isEmpty()) {
					userIds.add(userId);
				} else {
					userIds.add(userId);
					for (User object : childernUsers) {
						userIds.add(object.getId());
					}
				}
			}

			Optional<List<Warehouse>> optionalWarehouseList;
			if(whSize>0){
				optionalWarehouseList = warehousesRepository.findAllByUserIdInAndDeleteDate(userIds,null,new PageRequest(offset,whSize));
			}else {
				optionalWarehouseList = warehousesRepository.findAllByUserIdInAndDeleteDate(userIds,null);
			}

			if (optionalWarehouseList.isPresent()) {
				List<Warehouse> warehouseList = optionalWarehouseList.get();
				if (search != "") {
					if (warehouseList.size() > 0 && Pattern.matches(".*\\S.*", search)) {
						warehouseList = warehouseList.stream().filter(warehouse ->
								warehouse.getName().contains(search)).collect(Collectors.toList());
					}
				}

				List<Long> wareHouseIds = new ArrayList<>();

				for (Warehouse warehouse : warehouseList) {
					wareHouseIds.add(warehouse.getId());
				}

				Optional<List<Inventory>> optionalInventoryList = inventoryRepository.findAllByWarehouseIdInAndDeleteDate(wareHouseIds,null);

				if (optionalInventoryList.isPresent()) {
					List<Inventory> inventoryList = optionalInventoryList.get();
					List<String> lastDataIds = new ArrayList<>();

					for (Inventory inventory : inventoryList) {
						lastDataIds.add(inventory.getLastDataId());
					}

					Optional<List<MonogoInventoryLastData>> optionalMongoInventoryLastDataList = mongoInventoryLastDataRepository.findAllBy_idIn(lastDataIds);

					if(optionalMongoInventoryLastDataList.isPresent()) {
						List<MonogoInventoryLastData> mongoInventoryLastDataList = optionalMongoInventoryLastDataList.get();
						for(Warehouse warehouse : warehouseList){
							List<Inventory> inventoriesForOneWareHouse = inventoryList
									.stream()
									.filter(inventory -> inventory.getWarehouseId().equals(warehouse.getId())).collect(Collectors.toList());
							List<InventoryLastDataResponse> lastInvData = new ArrayList<>();
							for (Inventory inventory : inventoriesForOneWareHouse){
								Optional<MonogoInventoryLastData> lastTempAndHum = mongoInventoryLastDataList.stream().filter(mongoInventoryLastData -> mongoInventoryLastData.getInventoryId().equals(inventory.getId())).findFirst();
								if (lastTempAndHum.isPresent()) {
									mongoInventoryLastDataList.remove(lastTempAndHum.get());
									lastInvData.add(InventoryLastDataResponse
											.builder()
											.wareHouseId(warehouse.getId())
											.inventoryName(inventory.getName())
											.lastUpdated(inventory.getLastUpdate())
											.lastTemperature(lastTempAndHum.get().getTemperature())
											.lastHumidity(lastTempAndHum.get().getHumidity())
											.build());
								}
							}

							results.add(WareHouseInvLastDataResponse
									.builder()
									.wareHouseName(warehouse.getName())
									.inventoryData(lastInvData)
									.build());
							}
						logger.info("****************** getWareHouseInvLastData ENDED ******************");
						builder.setEntity(results);
						builder.setStatusCode(200);
						builder.setMessage("Data Found");
						builder.setSize(results.size());
						builder.setSuccess(true);
						return builder.build();
					}else {
						builder.setEntity(results);
						builder.setStatusCode(401);
						builder.setMessage("No Search Data Found");
						builder.setSize(results.size());
						builder.setSuccess(false);
						return builder.build();
					}
				}
			} else {
				builder.setEntity(results);
				builder.setStatusCode(401);
				builder.setMessage("No WareHouses Data Found");
				builder.setSize(results.size());
				builder.setSuccess(false);
				return builder.build();
			}
		}
		builder.setEntity(results);
		builder.setStatusCode(401);
		builder.setMessage("No Data Found");
		builder.setSize(results.size());
		builder.setSuccess(false);
		return builder.build();

	}

}





	

