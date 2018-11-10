package chickenlib;

import java.util.ArrayList;
import java.util.HashSet;

public class CknTaskManager {

    private static CknTaskManager instance;

    // Create this class as a singleton class, we wouldn't wan't multiple task managers running.
    public CknTaskManager(){
        taskObjects = new ArrayList<>();
        instance = this;
    }

    public static CknTaskManager getInstance() {
        return instance;
    }

    public enum TaskType {
        PRECONTINUOUS,
        POSTCONTINUOUS;
    }

    public interface Task {

        /**
         * The method to be called in every precontinuous call.
         */
        void preContinuous();

        /**
         * The method to be called in every postcontinuous call.
         */
        void postContinuous();

    }

    public class TaskObject {

        private Task task;
        private HashSet<TaskType> taskTypes;

        public TaskObject(Task task){
            this.task = task;
            taskTypes = new HashSet<>();
        }

        /**
         * Returns true if this task object has a certain task type.
         * @param type
         * @return
         */
        public boolean hasType(TaskType type){
            if(taskTypes.contains(type)) return true;
            return false;
        }

        public boolean noType(){
            return taskTypes.isEmpty();
        }

        /**
         * Adds a task type to this object, signifying it does need to be executed
         * @param type
         */
        public void addType(TaskType type){
            if(!taskTypes.contains(type))
            taskTypes.add(type);
        }

        /**
         * Removes a task type from the task manager.s
         * @param type
         */
        public void removeType(TaskType type){
            if(taskTypes.contains(type)) taskTypes.remove(type);
        }

        /**
         * Checks to see if this TaskObject has the same Task object as the one passed in.
         * @param task
         * @return
         */
        public boolean equals(Task task){
            return this.task == task;
        }

        public Task getTask(){
            return task;
        }

    }

    // This array list contains all TaskObjects
    ArrayList<TaskObject> taskObjects;

    public void registerTask(Task task, TaskType type){
        TaskObject taskObject = null;

        // First, check if there is already a task object for this task.
        for(TaskObject object : taskObjects){
            if(object.equals(task)){
                taskObject = object;
                break;
            }
        }

        // We handle the task object differently whether there is one or isn't one already registered.
        if(taskObject != null){
            taskObject.addType(type);
        } else {
            taskObject = new TaskObject(task);
            taskObject.addType(type);
            taskObjects.add(taskObject);
        }
    }

    /**
     * Removes a task from the task manager.
     * @param task The task that is being unregistered.
     * @param type The type of task to unregister.
     */
    public void unregisterTask(Task task, TaskType type){
        for(TaskObject object : taskObjects){
            if(object.equals(task)){
                object.removeType(type);
                if(object.noType()) taskObjects.remove(object);
            }
        }
    }

    public void executeTasks(TaskType type){
        for(TaskObject object : taskObjects){
            if(object.hasType(type)){
                switch(type){
                    case PRECONTINUOUS:
                        object.getTask().preContinuous();
                        break;
                    case POSTCONTINUOUS:
                        object.getTask().postContinuous();
                        break;
                }
            }
        }
    }



}
