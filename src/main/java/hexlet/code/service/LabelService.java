package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;

    public Label getLabelById(long id) {
        return labelRepository.findById(id).orElseThrow();
    }

    public List<Label> getLabels() {
        return labelRepository.findAll();
    }

    public Label createLabel(LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    public Label updateLabel(long id, LabelDto labelDto) {
        Label label = labelRepository.findById(id).orElseThrow();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    public void deleteLabel(long id) {
        Label label = labelRepository.findById(id).orElseThrow();
        labelRepository.delete(label);
    }
}
