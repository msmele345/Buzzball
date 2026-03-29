export interface LoaderProps {
    outerClassName?: string;
    itemClassName?: string;
}

const Loader = ({outerClassName, itemClassName}: LoaderProps) => {
    return (
        <div className={outerClassName ? outerClassName : "space-y-2"}>
            {[...Array(5)]
                .map((_, i) => (
                    <div key={i} className={`${itemClassName ?? ''} animate-pulse h-8 bg-bg-surface rounded`}/>
                ))}
        </div>
    )
};

export default Loader;