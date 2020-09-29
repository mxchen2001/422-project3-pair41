package scoreannotation;

import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes("scoreannotation.Score")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ScoreAnnotationProcessor extends AbstractProcessor {
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Score.class)) {
            // The following line assumes annotatedElement is a method because
            // the Score annotation can only be put on methods.
            String classname = ((TypeElement) annotatedElement.getEnclosingElement()).getQualifiedName().toString();
            String name = annotatedElement.getSimpleName().toString();
            int score = annotatedElement.getAnnotation(Score.class).value();
            System.err.println(classname + ":" + name + ":" + score);
        }
        return true;
    }
}

