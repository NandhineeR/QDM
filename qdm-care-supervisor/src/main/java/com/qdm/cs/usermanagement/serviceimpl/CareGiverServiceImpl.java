package com.qdm.cs.usermanagement.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareGiver;
import com.qdm.cs.usermanagement.entity.CareProvider;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.repository.CareGiverRepository;
import com.qdm.cs.usermanagement.repository.CareProviderRepository;
import com.qdm.cs.usermanagement.repository.CategoryRepository;
import com.qdm.cs.usermanagement.repository.SkillsRepository;
import com.qdm.cs.usermanagement.repository.UploadProfileRepository;
import com.qdm.cs.usermanagement.service.CareGiverService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CareGiverServiceImpl implements CareGiverService {

	CategoryRepository categoryRepository;
	CareGiverRepository careGiverRepository;
	ModelMapper modelMapper;
	UploadProfileRepository uploadProfileRepository;
	SkillsRepository skillsRepository;
	CareProviderRepository careProviderRepository;

	@Autowired
	public CareGiverServiceImpl(CategoryRepository categoryRepository, CareGiverRepository careGiverRepository,
			ModelMapper modelMapper, UploadProfileRepository uploadProfileRepository, SkillsRepository skillsRepository,
			CareProviderRepository careProviderRepository) {
		super();
		this.categoryRepository = categoryRepository;
		this.careGiverRepository = careGiverRepository;
		this.modelMapper = modelMapper;
		this.uploadProfileRepository = uploadProfileRepository;
		this.skillsRepository = skillsRepository;
		this.careProviderRepository = careProviderRepository;
	}

	@Override
	public List<CareGiver> getCareGiver(Integer pageNo, Integer pageSize, String sortDirec, String sortfield) {
		// Pageable paging = PageRequest.of(pageNo, pageSize,Sort.by("careGiverName"));
		Pageable paging = PageRequest.of(pageNo, pageSize,
				sortDirec.toLowerCase().startsWith("desc") ? Direction.DESC : Direction.ASC, sortfield);
		Page<CareGiver> pagedResult = careGiverRepository.findAll(paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareGiver>();
	}

	@Override
	public Optional<CareGiver> getCareGiverById(int careGiverId) {
		return careGiverRepository.findByCareGiverId(careGiverId);
	}

	@Override
	public CareGiver updateClientsCount(long careGiverId, int clientsCount) {
		Optional<CareGiver> careGiverUpdateDate = careGiverRepository.findById(careGiverId);
		if (careGiverUpdateDate.isPresent()) {
			careGiverUpdateDate.get().setCareGiverId(careGiverId);
			careGiverUpdateDate.get().setClientsCount(clientsCount);
			return careGiverRepository.save(careGiverUpdateDate.get());
		}
		return careGiverUpdateDate.get();
	}

	@Override
	public CareGiver updateClientsActiveStatus(long careGiverId, Status activeStatus) {
		Optional<CareGiver> careGiverUpdateDate = careGiverRepository.findById(careGiverId);
		if (careGiverUpdateDate.isPresent()) {
			careGiverUpdateDate.get().setCareGiverId(careGiverId);
			careGiverUpdateDate.get().setActiveStatus(activeStatus);
			return careGiverRepository.save(careGiverUpdateDate.get());
		}
		return careGiverUpdateDate.get();
	}

	@Override
	public List<Category> getCategoryListById(Collection<Integer> category) {
		List<Category> data = new ArrayList<>();
		for (Integer categoryData : category) {
			Category categoryList = categoryRepository.findByCategoryId(categoryData);
			data.add(categoryList);
		}
		return data;
	}

	@Override
	public CareGiver addCareGiver(FormDataDTO formDataDTO) {
		CareGiver careGiver = modelMapper.map(formDataDTO, CareGiver.class);
		if (careGiver.getCareprovider().size() != 0) {
			for (Long careProvider : careGiver.getCareprovider()) {
				Optional<CareProvider> carePro = careProviderRepository.findById(careProvider);
				if (carePro.isPresent()) {
					int count = carePro.get().getCareGiversCount();
					carePro.get().setCareGiversCount(count + 1);
					careProviderRepository.save(carePro.get());
				}
			}
		}
		if (careGiver.getUploadPhoto() != null) {
			UploadProfile uploadProfile = null;
			try {
				String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
				uploadProfile = UploadProfile.builder().fileName(fileName)
						.fileType(formDataDTO.getUploadPhoto().getContentType())
						.data(formDataDTO.getUploadPhoto().getBytes()).size(formDataDTO.getUploadPhoto().getSize())
						.build();
				careGiver.setUploadPhoto(uploadProfile);
			} catch (IOException e) {
				log.error("Error Occured In CareGiversService AddCareGiver ProfileUpload With Id : "
						+ careGiver.getCareGiverId());
			}
		}
		return careGiverRepository.save(careGiver);
	}

	@Override
	public CareGiver updateCareGiver(FormDataDTO formDataDTO) {
		Optional<CareGiver> careGiverUpdateDate = careGiverRepository.findById(formDataDTO.getCareGiverId());
		if (careGiverUpdateDate.isPresent()) {
			careGiverUpdateDate.get()
					.setActiveStatus(formDataDTO.getActiveStatus() != null ? formDataDTO.getActiveStatus()
							: careGiverUpdateDate.get().getActiveStatus());
			careGiverUpdateDate.get().setAddress(formDataDTO.getAddress() != null ? formDataDTO.getAddress()
					: careGiverUpdateDate.get().getAddress());
			careGiverUpdateDate.get()
					.setCareGiverName(formDataDTO.getCareGiverName() != null ? formDataDTO.getCareGiverName()
							: careGiverUpdateDate.get().getCareGiverName());
			careGiverUpdateDate.get().setCategory(formDataDTO.getCategory() != null ? formDataDTO.getCategory()
					: careGiverUpdateDate.get().getCategory());
			careGiverUpdateDate.get().setClientsCount(formDataDTO.getClientsCount() != 0 ? formDataDTO.getClientsCount()
					: careGiverUpdateDate.get().getClientsCount());
			careGiverUpdateDate.get().setEmailId(formDataDTO.getEmailId() != null ? formDataDTO.getEmailId()
					: careGiverUpdateDate.get().getEmailId());
			careGiverUpdateDate.get().setMobileNo(formDataDTO.getMobileNo() != 0 ? formDataDTO.getMobileNo()
					: careGiverUpdateDate.get().getMobileNo());
			careGiverUpdateDate.get().setSkills(
					formDataDTO.getSkills() != null ? formDataDTO.getSkills() : careGiverUpdateDate.get().getSkills());
			careGiverUpdateDate.get().setLicenseNo(formDataDTO.getLicenseNo() != null ? formDataDTO.getLicenseNo()
					: careGiverUpdateDate.get().getLicenseNo());
			careGiverUpdateDate.get().setExperience(formDataDTO.getExperience() != 0 ? formDataDTO.getExperience()
					: careGiverUpdateDate.get().getExperience());

			if(formDataDTO.getCareprovider() != null) {
			for (Long careProExistingList : careGiverUpdateDate.get().getCareprovider()) {
				for (Long careProNewList : formDataDTO.getCareprovider()) {
					Optional<CareProvider> careProviderById = careProviderRepository.findById(careProNewList);
					if (careProviderById.isPresent()) {
						if (careProExistingList == careProNewList) {
							careProviderById.get().setCareGiversCount(careProviderById.get().getCareGiversCount());
						} else {
							careProviderById.get().setCareGiversCount(careProviderById.get().getCareGiversCount() + 1);
						}
					}
				}
			}
			}

			careGiverUpdateDate.get()
					.setCareprovider(formDataDTO.getCareprovider() != null ? formDataDTO.getCareprovider()
							: careGiverUpdateDate.get().getCareprovider());

			if (formDataDTO.getUploadPhoto() != null) {
				String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
				try {
					careGiverUpdateDate.get()
							.setUploadPhoto(new UploadProfile(formDataDTO.getUploadPhoto().getOriginalFilename(),
									formDataDTO.getUploadPhoto().getContentType(),
									formDataDTO.getUploadPhoto().getBytes(), formDataDTO.getUploadPhoto().getSize()));
				} catch (IOException e) {
					log.info("Error Occured at UpdateCareGiver Photo Upload");
					e.printStackTrace();
				}
				CareGiver savedCareGiver = careGiverRepository.save(careGiverUpdateDate.get());
				return savedCareGiver;
			} else {
				careGiverUpdateDate.get().setUploadPhoto(careGiverUpdateDate.get().getUploadPhoto());
			}
		}
		return careGiverUpdateDate.get();
	}

	@Override
	public List<CareGiver> getAllCareGiversListCount() {
		return careGiverRepository.findAll();
	}

	@Override
	public UploadProfile getFile(int fileId) {
		return uploadProfileRepository.findById(fileId).get();
	}

	@Override
	public List<CareGiver> searchCareGiver(Integer pageNo, Integer pageSize, String careGiverName, String sortDirec,
			String sortfield) {
		Pageable paging = PageRequest.of(pageNo, pageSize,
				sortDirec.toLowerCase().startsWith("desc") ? Direction.DESC : Direction.ASC, sortfield);
		// Pageable paging = PageRequest.of(pageNo, pageSize,Sort.by("careGiverName"));
		Page<CareGiver> pagedResult = careGiverRepository.findByCareGiverName(careGiverName.toLowerCase(), paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareGiver>();
	}

	@Override
	public List<CareGiver> searchAllCareGiversListCount(String careGiverName) {
		return careGiverRepository.findByCareGiverName(careGiverName.toLowerCase());
	}

	@Override
	public List<Skills> getSkillsListById(Collection<Integer> skills) {
		List<Skills> data = new ArrayList<>();
		for (Integer skillData : skills) {
			Skills skillList = skillsRepository.findBySkillId(skillData);
			data.add(skillList);
		}
		return data;
	}

	@Override
	public CareGiver deleteCareProviderMapping(Long careGiverId, Long careProviderId) {
		List<Long> obj = new ArrayList<Long>();
		Optional<CareGiver> careGiver = careGiverRepository.findById(careGiverId);
		if (careGiver.isPresent()) {
			for (Long careProvider : careGiver.get().getCareprovider()) {
				obj.add(careProvider);
				if (careProvider == careProviderId) {
					obj.remove(careProviderId);
					Optional<CareProvider> carePro = careProviderRepository.findById(careProviderId);
					if (carePro.isPresent()) {
						carePro.get().setCareGiversCount(carePro.get().getCareGiversCount() - 1);
					}
				}
			}
			careGiver.get().setCareprovider(obj);
			careGiverRepository.save(careGiver.get());
		}
		return careGiver.get();
	}

}
