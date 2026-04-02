export interface LoaderProps {
    outerClassName?: string;
    itemClassNames?: string;
}

const Loader = ({outerClassName, itemClassNames}: LoaderProps) => {
    return (
        <div className={outerClassName ? outerClassName : "space-y-2"}>
            {[...Array(5)]
                .map((_, i) => (
                    <div key={i} className={itemClassNames ? itemClassNames : 'animate-pulse h-8 bg-bg-surface rounded'}/>
                ))}
        </div>
    )
};

export default Loader;