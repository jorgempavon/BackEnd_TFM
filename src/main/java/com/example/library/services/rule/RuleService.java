package com.example.library.services.rule;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.rule.RuleAndRuleDTO;
import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleUpdateDTO;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.repository.rule.RuleRepository;
import com.example.library.services.user.AdminService;
import com.example.library.util.ValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;
    private final AdminService adminService;

    public RuleService(RuleRepository ruleRepository,AdminService adminService){
        this.ruleRepository = ruleRepository;
        this.adminService = adminService;
    }

    public RuleDTO findById(Long id){
        if(!this.ruleRepository.existsById(id)){
            throw new NotFoundException("No existe ninguna regla con el id: "+id.toString());
        }
        Rule rule = this.ruleRepository.findById(id).get();

        String adminFullName = this.adminService.getUserFullNameByAdmin(rule.getAdmin());
        return new RuleDTO(rule.getId(), rule.getNumPenalties(),rule.getDays(),rule.getName()
                ,adminFullName,rule.getType());
    }

    public List<RuleDTO> findByNameAndNumMimPenalties(String name, Integer numMimPenalties) {
        Specification<Rule> spec = Specification.where(null);

        spec = ValidationUtils.buildQueryStringByField(spec,"name",name);
        spec = ValidationUtils.buildQueryIntegerByField(spec,"numMimPenalties",numMimPenalties);

        List<Rule> rules = this.ruleRepository.findAll(spec);
        List<RuleDTO> responseList= new ArrayList<>();

        for (Rule rule : rules) {
            String adminFullName = this.adminService.getUserFullNameByAdmin(rule.getAdmin());
            RuleDTO newRuleDto = new RuleDTO(rule.getId(), rule.getNumPenalties(),rule.getDays()
                    ,rule.getName(),adminFullName,rule.getType());
            responseList.add(newRuleDto);
        }

        return responseList;
    }
    @Transactional
    public RuleDTO update(Long id, RuleUpdateDTO ruleUpdateDTO){
        this.checkRuleExistenceInUpdate(id,ruleUpdateDTO);
        Rule currentRule = this.ruleRepository.findById(id).get();

        if(ValidationUtils.isValidAndChangedString(ruleUpdateDTO.getName(),currentRule.getName())){
            currentRule.setName(ruleUpdateDTO.getName());
        }
        if(ValidationUtils.isValidAndChangedInteger(ruleUpdateDTO.getNumPenalties(),currentRule.getNumPenalties())){
            currentRule.setNumPenalties(ruleUpdateDTO.getNumPenalties());
        }
        if(ValidationUtils.isValidAndChangedInteger(ruleUpdateDTO.getDays(),currentRule.getDays())){
            currentRule.setDays(ruleUpdateDTO.getDays());
        }
        this.ruleRepository.save(currentRule);

        String adminFullName = this.adminService.getUserFullNameByAdmin(currentRule.getAdmin());
        return new RuleDTO(
                currentRule.getId(),
                currentRule.getNumPenalties(),
                currentRule.getDays(),
                currentRule.getName(),
                adminFullName,
                currentRule.getType()
        );
    }
    private void checkRuleExistenceInUpdate(Long id, RuleUpdateDTO ruleUpdateDTO){
        if (!this.ruleRepository.existsById(id)){
            throw new NotFoundException("No existe la regla con el id: "+ id);
        }
        Rule rule  = this.ruleRepository.findById(id).get();

        String newName = ruleUpdateDTO.getName();
        Integer newNumPenalties = ruleUpdateDTO.getNumPenalties();
        Integer newDays = ruleUpdateDTO.getDays();
        if (this.ruleRepository.existsByNameAndNumPenaltiesAndDaysAndTypeAndIdNot(newName,newNumPenalties,newDays,
                rule.getType(),id)){
            throw new BadRequestException("Ya existe una regla con los atributos proporcionados");
        }

    }
    private void checkRuleExistenceInCreate(RuleCreateDTO ruleCreateDTO,String type){
        String newName = ruleCreateDTO.getName();
        Integer newNumPenalties = ruleCreateDTO.getNumPenalties();
        Integer newDays = ruleCreateDTO.getDays();

        if (this.ruleRepository.existsByNameAndNumPenaltiesAndDaysAndType(newName,newNumPenalties,newDays,type)){
            throw new BadRequestException("Ya existe una regla con los atributos proporcionados");
        }

    }
    @Transactional
    public RuleAndRuleDTO create(Long creatorUserId, RuleCreateDTO ruleCreateDTO, String type){
        this.checkRuleExistenceInCreate(ruleCreateDTO,type);

        Admin adminCreator = this.adminService.getAdminByUserId(creatorUserId);
        Rule rule = new Rule(
                ruleCreateDTO.getName(),
                ruleCreateDTO.getNumPenalties(),
                ruleCreateDTO.getDays(),
                adminCreator,
                type
        );
        this.ruleRepository.save(rule);

        String adminFullName = this.adminService.getUserFullNameByAdmin(rule.getAdmin());
        RuleDTO ruleDTO = new RuleDTO(
                rule.getId(),
                rule.getNumPenalties(),
                rule.getDays(),
                rule.getName(),
                adminFullName,
                rule.getType()
        );
        return new RuleAndRuleDTO(rule,ruleDTO);
    }

    public void delete(Long id){
        if(this.ruleRepository.existsById(id)){
            Rule rule = this.ruleRepository.findById(id).get();
            this.ruleRepository.delete(rule);
        }
    }

    public String getRuleNameByRule(Rule rule){
        if(!this.ruleRepository.existsById(rule.getId())){
            throw new NotFoundException("No existe ninguna regla con el id proporcionado");
        }
        return rule.getName();
    }

}
