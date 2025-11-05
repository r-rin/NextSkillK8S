package ukma.springboot.nextskill.course;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ukma.springboot.nextskill.common.dto.views.SectionView;

@Getter
public class SectionCreateEvent extends ApplicationEvent {
    private final SectionView sectionView;

    public SectionCreateEvent(Object source, SectionView sectionView) {
        super(source);
        this.sectionView = sectionView;
    }
}
